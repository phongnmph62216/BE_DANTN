package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Entity.ChatSession;
import com.example.be_dantn.Entity.ChatMessage;
import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Entity.ThongBao;
import com.example.be_dantn.Repository.ChatSessionRepository;
import com.example.be_dantn.Repository.ChatMessageRepository;
import com.example.be_dantn.Repository.KhachHangRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.Repository.ThongBaoRepository;
import com.example.be_dantn.sevicer.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Override
    @Transactional
    public ChatSession getOrCreateSession(String sessionCode, Long customerId, String name) {
        Optional<ChatSession> existing = chatSessionRepository.findBySessionCode(sessionCode);
        if (existing.isPresent()) {
            ChatSession session = existing.get();
            // If it was closed, re-open it for support
            if (session.getTrangThai() == 2) {
                session.setTrangThai(0);
                session.setNhanVien(null);
                session.setNgayCapNhatCuoi(LocalDateTime.now());
                return chatSessionRepository.save(session);
            }
            return session;
        }

        KhachHang khachHang = null;
        if (customerId != null) {
            khachHang = khachHangRepository.findById(customerId).orElse(null);
        }

        ChatSession session = ChatSession.builder()
                .sessionCode(sessionCode)
                .khachHang(khachHang)
                .visitorName(name != null ? name : "Khách vãng lai")
                .trangThai(0) // 0: Chờ tiếp nhận
                .ngayCapNhatCuoi(LocalDateTime.now())
                .build();

        return chatSessionRepository.save(session);
    }

    @Override
    @Transactional
    public ChatMessage saveMessage(String sessionCode, String senderType, Long senderId, String senderName, String content) {
        ChatSession chatSession = chatSessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found: " + sessionCode));

        ChatMessage message = ChatMessage.builder()
                .chatSession(chatSession)
                .senderType(senderType)
                .senderId(senderId)
                .senderName(senderName)
                .noiDung(content)
                .build();

        chatSession.setNgayCapNhatCuoi(LocalDateTime.now());
        chatSessionRepository.save(chatSession);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Generate ThongBao notification for staff if a customer sends a message in a waiting session (status 0)
        if ("CUSTOMER".equalsIgnoreCase(senderType) && chatSession.getTrangThai() == 0) {
            String notificationContent = "Khách " + senderName + " vừa gửi tin nhắn ở phiên #" + chatSession.getId() + ": " + content;
            // Check if there is already an unread notification for this session code to avoid spamming
            boolean exists = thongBaoRepository.findAllByOrderByNgayTaoDesc().stream()
                    .anyMatch(tb -> tb.getTrangThai() == 0 && tb.getNoiDung().contains("ở phiên #" + chatSession.getId()));
            
            if (!exists) {
                ThongBao thongBao = ThongBao.builder()
                        .tieuDe("Có tin nhắn mới")
                        .noiDung(notificationContent)
                        .trangThai(0) // 0: Chưa đọc
                        .build();
                thongBaoRepository.save(thongBao);
            }
        }

        return savedMessage;
    }

    @Override
    @Transactional
    public ChatSession acceptSession(String sessionCode, Long staffId) {
        ChatSession chatSession = chatSessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found: " + sessionCode));

        NhanVien nhanVien = nhanVienRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + staffId));

        chatSession.setTrangThai(1); // 1: Đang hoạt động
        chatSession.setNhanVien(nhanVien);
        chatSession.setNgayCapNhatCuoi(LocalDateTime.now());
        chatSessionRepository.save(chatSession);

        // Add a system message notifying acceptance
        ChatMessage systemMsg = ChatMessage.builder()
                .chatSession(chatSession)
                .senderType("SYSTEM")
                .senderName("Hệ thống")
                .noiDung("Nhân viên " + nhanVien.getHoVaTen() + " (Mã: " + nhanVien.getMaNhanVien() + ") đã tiếp nhận hỗ trợ bạn.")
                .build();
        chatMessageRepository.save(systemMsg);

        // Mark all ThongBao notifications related to this session as read
        List<ThongBao> thongBaos = thongBaoRepository.findAll();
        for (ThongBao tb : thongBaos) {
            if (tb.getTrangThai() == 0 && tb.getNoiDung() != null && tb.getNoiDung().contains("ở phiên #" + chatSession.getId())) {
                tb.setTrangThai(1); // Đã đọc
                thongBaoRepository.save(tb);
            }
        }

        return chatSession;
    }

    @Override
    @Transactional
    public ChatSession closeSession(String sessionCode) {
        ChatSession chatSession = chatSessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found: " + sessionCode));

        chatSession.setTrangThai(2); // 2: Đã đóng
        chatSession.setNgayCapNhatCuoi(LocalDateTime.now());
        chatSessionRepository.save(chatSession);

        // Add a system message notifying closure
        ChatMessage systemMsg = ChatMessage.builder()
                .chatSession(chatSession)
                .senderType("SYSTEM")
                .senderName("Hệ thống")
                .noiDung("Phiên hỗ trợ đã đóng.")
                .build();
        chatMessageRepository.save(systemMsg);

        return chatSession;
    }

    @Override
    @Transactional
    public void handleDisconnect(String sessionCode) {
        chatSessionRepository.findBySessionCode(sessionCode).ifPresent(session -> {
            session.setNgayCapNhatCuoi(LocalDateTime.now());
            chatSessionRepository.save(session);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSession> getSessions(Integer trangThai) {
        if (trangThai != null) {
            return chatSessionRepository.findAllByTrangThaiOrderByNgayCapNhatCuoiDesc(trangThai);
        }
        return chatSessionRepository.findAllByOrderByNgayCapNhatCuoiDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(String sessionCode) {
        return chatMessageRepository.findAllByChatSessionSessionCodeOrderByNgayTaoAsc(sessionCode);
    }
}
