package com.example.be_dantn.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonResponse {

    private Long id;

    private Long idNhanVien;

    private Long idKhachHang;

    private Long idPhieuGiamGia;

    private String maHoaDon;

    private String loaiHoaDon;

    private BigDecimal phiVanChuyen;

    private BigDecimal soTienGoc;

    private BigDecimal soTienGiam;

    private BigDecimal tongTienThanhToan;

    private String tenKhachHang;

    private String diaChiKhachHang;

    private String soDienThoai;

    private String trangThai;

    private String ghiChu;

    private LocalDateTime ngayNhanHang;

    private LocalDateTime ngaySua;

    private LocalDateTime ngayTao;

    private LocalDateTime ngayThanhToan;

    private String maQr;
}