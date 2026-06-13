package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String maHoaDon;

    // 0: Online, 
    // 1: Tại quầy 
    // 2: Giao hàng
    private Integer loaiHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    private BigDecimal soTienGoc;
    private BigDecimal soTienGiam;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTienThanhToan;

    private LocalDateTime ngayThanhToan;
    private LocalDateTime ngayNhanHang;

    // Các bước đẳng cấp
    // 0: Tạo mới
    // 1: Chờ xác nhận
    // 2: Đã xác nhận
    // 3: Đang giao
    // 4: Hoàn thành
    // 5: Đã hủy
    private Integer trangThai;

    @Column(columnDefinition = "nvarchar(1000)")
    private String ghiChu;

    @Column(columnDefinition = "nvarchar(1000)")
    private String diaChi;

    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;

    private String nguoiTao;
    private String nguoiSua;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> danhSachChiTiet;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        ngaySua = LocalDateTime.now();

        if (maHoaDon == null || maHoaDon.isBlank()) {
            maHoaDon = "HD" + System.currentTimeMillis();
        }

        if (loaiHoaDon == null) {
            loaiHoaDon = 0;
        }

        if (trangThai == null) {
            trangThai = 0;
        }

        if (soTienGoc == null) {
            soTienGoc = BigDecimal.ZERO;
        }

        if (soTienGiam == null) {
            soTienGiam = BigDecimal.ZERO;
        }

        if (phiVanChuyen == null) {
            phiVanChuyen = BigDecimal.ZERO;
        }

        if (tongTienThanhToan == null) {
            tongTienThanhToan = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}