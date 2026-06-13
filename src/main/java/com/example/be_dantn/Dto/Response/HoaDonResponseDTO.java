package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonResponseDTO {

    private Long id;

    private String maHoaDon;

    private Integer loaiHoaDon;

    private String tenKhachHang;

    private String soDienThoai;

    private String emailKhachHang;

    private String tenNhanVien;

    private String maNhanVien;

    private String maPhieuGiamGia;

    private String tenPhieuGiamGia;

    private BigDecimal soTienGoc;

    private BigDecimal soTienGiam;

    private BigDecimal phiVanChuyen;

    private BigDecimal tongTienThanhToan;

    private LocalDateTime ngayTao;

    private LocalDateTime ngayThanhToan;

    private LocalDateTime ngayNhanHang;

    private Integer trangThai;

    private String ghiChu;

    private String diaChi;
}