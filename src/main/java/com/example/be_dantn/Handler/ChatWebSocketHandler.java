package com.example.be_dantn.Handler;

import com.example.be_dantn.Entity.ChatSession;
import com.example.be_dantn.Entity.ChatMessage;
import com.example.be_dantn.sevicer.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    // Map: sessionCode -> Set of WebSocketSessions for customer
    private static final Map<String, Set<WebSocketSession>> customerSessions = new ConcurrentHashMap<>();

    // Set of active staff WebSocketSessions
    private static final Set<WebSocketSession> staffSessions = new CopyOnWriteArraySet<>();

    // Map: WebSocketSession -> Connection details
    private static final Map<WebSocketSession, ConnectionInfo> connectionDetails = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static class ConnectionInfo {
        public String role; // "customer" or "staff"
        public Long userId; // customerId or staffId
        public String sessionCode; // only for customer
        public String name;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        Map<String, String> params = parseQueryParams(query);

        ConnectionInfo info = new ConnectionInfo();
        info.role = params.get("role");

        if ("customer".equalsIgnoreCase(info.role)) {
            info.sessionCode = params.get("sessionCode");
            info.name = params.getOrDefault("name", "Khách vãng lai");
            String custIdStr = params.get("customerId");
            if (custIdStr != null && !custIdStr.isEmpty() && !"null".equalsIgnoreCase(custIdStr)) {
                info.userId = Long.parseLong(custIdStr);
            }
            connectionDetails.put(session, info);

            // Register session in memory
            customerSessions.computeIfAbsent(info.sessionCode, k -> new CopyOnWriteArraySet<>()).add(session);

            // Ensure database session exists
            chatService.getOrCreateSession(info.sessionCode, info.userId, info.name);

            // Broadcast status change to staff
            broadcastToStaff(Map.of(
                    "type", "CUSTOMER_STATUS",
                    "sessionCode", info.sessionCode,
                    "name", info.name,
                    "status", "ONLINE"
            ));

        } else if ("staff".equalsIgnoreCase(info.role)) {
            String staffIdStr = params.get("staffId");
            if (staffIdStr != null && !staffIdStr.isEmpty() && !"null".equalsIgnoreCase(staffIdStr)) {
                info.userId = Long.parseLong(staffIdStr);
            }
            info.name = params.getOrDefault("name", "Nhân viên");
            connectionDetails.put(session, info);

            // Register staff in memory
            staffSessions.add(session);

            // Broadcast that staff is online (optional)
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "type", "SYSTEM",
                    "content", "Kết nối hệ thống chat hỗ trợ thành công."
            ))));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ConnectionInfo info = connectionDetails.remove(session);
        if (info != null) {
            if ("customer".equalsIgnoreCase(info.role)) {
                Set<WebSocketSession> sessions = customerSessions.get(info.sessionCode);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        customerSessions.remove(info.sessionCode);
                        chatService.handleDisconnect(info.sessionCode);
                        // Notify staff that customer went offline
                        broadcastToStaff(Map.of(
                                "type", "CUSTOMER_STATUS",
                                "sessionCode", info.sessionCode,
                                "status", "OFFLINE"
                        ));
                    }
                }
            } else if ("staff".equalsIgnoreCase(info.role)) {
                staffSessions.remove(session);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ConnectionInfo info = connectionDetails.get(session);
        if (info == null) return;

        String payload = message.getPayload();
        Map<String, Object> data;
        try {
            data = objectMapper.readValue(payload, Map.class);
        } catch (Exception e) {
            return;
        }

        String type = (String) data.get("type");
        if ("CHAT".equalsIgnoreCase(type)) {
            String content = (String) data.get("content");
            if (content == null || content.trim().isEmpty()) return;

            if ("customer".equalsIgnoreCase(info.role)) {
                // Customer sending chat -> Save to DB, notify customer tabs and all staff
                ChatMessage chatMsg = chatService.saveMessage(info.sessionCode, "CUSTOMER", info.userId, info.name, content);
                
                Map<String, Object> responseMsg = Map.of(
                        "type", "CHAT",
                        "sessionCode", info.sessionCode,
                        "senderType", "CUSTOMER",
                        "senderName", info.name,
                        "content", content,
                        "time", chatMsg.getNgayTao().format(timeFormatter)
                );
                
                // Broadcast to customer sessions (other tabs) and all staff
                sendToCustomer(info.sessionCode, responseMsg);
                broadcastToStaff(responseMsg);

            } else if ("staff".equalsIgnoreCase(info.role)) {
                // Staff sending chat -> Save to DB, notify customer tabs and all staff
                String sessionCode = (String) data.get("sessionCode");
                if (sessionCode == null || sessionCode.isEmpty()) return;

                ChatMessage chatMsg = chatService.saveMessage(sessionCode, "STAFF", info.userId, info.name, content);

                Map<String, Object> responseMsg = Map.of(
                        "type", "CHAT",
                        "sessionCode", sessionCode,
                        "senderType", "STAFF",
                        "senderName", info.name,
                        "content", content,
                        "time", chatMsg.getNgayTao().format(timeFormatter)
                );

                // Send to the targeted customer and broadcast to all staff
                sendToCustomer(sessionCode, responseMsg);
                broadcastToStaff(responseMsg);
            }
        } else if ("TYPING".equalsIgnoreCase(type)) {
            Boolean isTyping = (Boolean) data.get("isTyping");
            if (isTyping == null) isTyping = false;

            if ("customer".equalsIgnoreCase(info.role)) {
                broadcastToStaff(Map.of(
                        "type", "TYPING",
                        "sessionCode", info.sessionCode,
                        "senderType", "CUSTOMER",
                        "isTyping", isTyping
                ));
            } else if ("staff".equalsIgnoreCase(info.role)) {
                String sessionCode = (String) data.get("sessionCode");
                if (sessionCode != null) {
                    sendToCustomer(sessionCode, Map.of(
                            "type", "TYPING",
                            "sessionCode", sessionCode,
                            "senderType", "STAFF",
                            "isTyping", isTyping
                    ));
                }
            }
        }
    }

    public void notifyAccept(String sessionCode, String staffName, String staffCode) {
        Map<String, Object> msg = Map.of(
                "type", "SYSTEM",
                "sessionCode", sessionCode,
                "senderType", "SYSTEM",
                "content", "Nhân viên " + staffName + " (Mã: " + staffCode + ") đã tiếp nhận hỗ trợ bạn.",
                "time", LocalDateTime.now().format(timeFormatter)
        );
        sendToCustomer(sessionCode, msg);
        broadcastToStaff(Map.of(
                "type", "SESSION_ACCEPTED",
                "sessionCode", sessionCode,
                "staffName", staffName
        ));
    }

    public void notifyClose(String sessionCode) {
        Map<String, Object> msg = Map.of(
                "type", "SYSTEM",
                "sessionCode", sessionCode,
                "senderType", "SYSTEM",
                "content", "Phiên hỗ trợ đã đóng.",
                "time", LocalDateTime.now().format(timeFormatter)
        );
        sendToCustomer(sessionCode, msg);
        broadcastToStaff(Map.of(
                "type", "SESSION_CLOSED",
                "sessionCode", sessionCode
        ));
    }

    private void sendToCustomer(String sessionCode, Object messageObj) {
        Set<WebSocketSession> sessions = customerSessions.get(sessionCode);
        if (sessions != null) {
            try {
                String json = objectMapper.writeValueAsString(messageObj);
                TextMessage textMsg = new TextMessage(json);
                for (WebSocketSession s : sessions) {
                    if (s.isOpen()) {
                        s.sendMessage(textMsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastToStaff(Object messageObj) {
        try {
            String json = objectMapper.writeValueAsString(messageObj);
            TextMessage textMsg = new TextMessage(json);
            for (WebSocketSession s : staffSessions) {
                if (s.isOpen()) {
                    s.sendMessage(textMsg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastNotificationToStaff(Object notificationData) {
        broadcastToStaff(Map.of(
                "type", "NOTIFICATION_ALERT",
                "data", notificationData
        ));
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new ConcurrentHashMap<>();
        if (query == null || query.isEmpty()) return params;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length > 1) {
                params.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length > 0) {
                params.put(kv[0], "");
            }
        }
        return params;
    }
}
