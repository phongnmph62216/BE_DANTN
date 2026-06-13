package com.example.be_dantn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vai_tro")
    private VaiTro vaiTro;

    private String maNhanVien;
    private String hoVaTen;
    private String soDienThoai;
    private String email;

    @JsonIgnore // Không bao giờ trả về mật khẩu trong bất kỳ response nào
    private String matKhau;

    private String cccd;
    private Integer gioiTinh;
    private LocalDate ngaySinh;
    private String diaChi;
    private Integer trangThai;
    private LocalDate ngayVaoLam;
    private String anh;

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = 1;
        }
        if (this.maNhanVien == null || this.maNhanVien.isEmpty()) {
            this.maNhanVien = "NV" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}
