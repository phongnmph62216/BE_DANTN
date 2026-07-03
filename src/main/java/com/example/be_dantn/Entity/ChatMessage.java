package com.example.be_dantn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_chat_session", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "khachHang", "nhanVien"})
    private ChatSession chatSession;

    @Column(name = "sender_type")
    private String senderType; // "CUSTOMER", "STAFF", "SYSTEM"

    @Column(name = "sender_id")
    private Long senderId; // ID of KhachHang or NhanVien (nullable)

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "noi_dung", length = 2000)
    private String noiDung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}
