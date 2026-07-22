package com.example.be_dantn.Controller;

import com.example.be_dantn.Entity.ChatSession;
import com.example.be_dantn.Entity.ChatMessage;
import com.example.be_dantn.Dto.ResponseObject;
import com.example.be_dantn.Handler.ChatWebSocketHandler;
import com.example.be_dantn.sevicer.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @GetMapping("/sessions")
    public ResponseEntity<ResponseObject<List<ChatSession>>> getSessions(
            @RequestParam(value = "trangThai", required = false) Integer trangThai) {
        List<ChatSession> sessions = chatService.getSessions(trangThai);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy danh sách phiên chat thành công", sessions));
    }

    @GetMapping("/sessions/{sessionCode}/messages")
    public ResponseEntity<ResponseObject<List<ChatMessage>>> getMessages(@PathVariable String sessionCode) {
        List<ChatMessage> messages = chatService.getMessages(sessionCode);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy lịch sử tin nhắn thành công", messages));
    }

    @PostMapping("/sessions/{sessionCode}/accept")
    public ResponseEntity<ResponseObject<ChatSession>> acceptSession(
            @PathVariable String sessionCode,
            @RequestParam("staffId") Long staffId) {
        try {
            ChatSession session = chatService.acceptSession(sessionCode, staffId);
            
            // Notify clients of connection acceptance
            if (session.getNhanVien() != null) {
                chatWebSocketHandler.notifyAccept(
                        sessionCode, 
                        session.getNhanVien().getHoVaTen(), 
                        session.getNhanVien().getMaNhanVien()
                );
            }
            return ResponseEntity.ok(new ResponseObject<>("success", "Tiếp nhận phiên chat thành công", session));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject<>("error", e.getMessage(), null));
        }
    }

    @PostMapping("/sessions/{sessionCode}/close")
    public ResponseEntity<ResponseObject<ChatSession>> closeSession(@PathVariable String sessionCode) {
        try {
            ChatSession session = chatService.closeSession(sessionCode);
            // Notify clients of closure
            chatWebSocketHandler.notifyClose(sessionCode);
            return ResponseEntity.ok(new ResponseObject<>("success", "Đóng phiên chat thành công", session));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject<>("error", e.getMessage(), null));
        }
    }

    @GetMapping("/internal/staff")
    public ResponseEntity<ResponseObject<List<com.example.be_dantn.Entity.NhanVien>>> getInternalStaffList() {
        List<com.example.be_dantn.Entity.NhanVien> staffList = chatService.getInternalStaffList();
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy danh sách nhân viên thành công", staffList));
    }

    @GetMapping("/internal/session")
    public ResponseEntity<ResponseObject<ChatSession>> getOrCreateInternalSession(
            @RequestParam("staff1Id") Long staff1Id,
            @RequestParam(value = "staff2Id", required = false) Long staff2Id) {
        ChatSession session = chatService.getOrCreateInternalSession(staff1Id, staff2Id);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy phiên chat nội bộ thành công", session));
    }
}
