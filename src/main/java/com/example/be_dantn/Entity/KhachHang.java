package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maKhachHang;
    private String hoTen;
    private String sdt;
    private String email;
    private Integer gioiTinh; // 0: Nữ, 1: Nam, 2: Khác
    private LocalDate ngaySinh;
    private Integer trangThai; // 0: Ngừng hoạt động, 1: Đang hoạt động

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaChi> danhSachDiaChi;

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = 1; // Mặc định là đang hoạt động
        }
        if (this.maKhachHang == null || this.maKhachHang.isEmpty()) {
            this.maKhachHang = "KH" + System.currentTimeMillis(); // Mã tự sinh cơ bản
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}
