package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_san_pham")
    private ChiTietSanPham chiTietSanPham;

    private BigDecimal donGia;

    private Integer soLuong;

    private BigDecimal thanhTien;

    private Integer trangThai;

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    private String nguoiTao;
    private String nguoiSua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();

        if (trangThai == null) {
            trangThai = 1;
        }

        if (soLuong == null) {
            soLuong = 1;
        }

        if (donGia == null) {
            donGia = BigDecimal.ZERO;
        }

        if (thanhTien == null) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();

        if (donGia != null && soLuong != null) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }
}