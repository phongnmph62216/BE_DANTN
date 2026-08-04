package com.example.be_dantn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_code", unique = true, nullable = false)
    private String sessionCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_khach_hang")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "danhSachDiaChi", "matKhau"})
    private KhachHang khachHang;

    @Column(name = "visitor_name")
    private String visitorName;

    @Column(name = "trang_thai")
    private Integer trangThai; // 0: Chờ tiếp nhận, 1: Đang hoạt động, 2: Đã đóng

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_nhan_vien")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "matKhau", "vaiTro"})
    private NhanVien nhanVien;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;

    @Column(name = "ngay_cap_nhat_cuoi")
    private LocalDateTime ngayCapNhatCuoi;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        this.ngaySua = LocalDateTime.now();
        this.ngayCapNhatCuoi = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngaySua = LocalDateTime.now();
    }
}
