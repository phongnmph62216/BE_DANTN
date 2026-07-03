package com.example.be_dantn.sevicer;

import com.example.be_dantn.Entity.ChatSession;
import com.example.be_dantn.Entity.ChatMessage;
import java.util.List;

public interface ChatService {
    ChatSession getOrCreateSession(String sessionCode, Long customerId, String name);
    ChatMessage saveMessage(String sessionCode, String senderType, Long senderId, String senderName, String content);
    ChatSession acceptSession(String sessionCode, Long staffId);
    ChatSession closeSession(String sessionCode);
    void handleDisconnect(String sessionCode);
    List<ChatSession> getSessions(Integer trangThai);
    List<ChatMessage> getMessages(String sessionCode);
}
