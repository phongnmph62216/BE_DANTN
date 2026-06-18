package com.example.be_dantn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "ma_hoa_don", unique = true)
    private String maHoaDon;

    @Column(name = "loai_hoa_don")
    private Integer loaiHoaDon; // 0 - Tại quầy, 1 - Online/Giao hàng

    @Column(name = "tong_tien_thanh_toan")
    private BigDecimal tongTienThanhToan;

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "trang_thai")
    private Integer trangThai; // 0-Chưa xác nhận, 1-Đã xác nhận, 2-Chờ giao, 3-Đang giao, 4-Đã hoàn thành, 5-Đã hủy

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}
