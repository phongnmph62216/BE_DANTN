package com.example.be_dantn.Dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class HoaDonRequest {

    private Long idNhanVien;

    private Long idKhachHang;

    private Long idPhieuGiamGia;

    @Size(max = 50, message = "Ma hoa don khong duoc qua 50 ky tu")
    private String maHoaDon;

    private String loaiHoaDon;

    @PositiveOrZero(message = "Phi van chuyen khong duoc am")
    private BigDecimal phiVanChuyen;

    private BigDecimal soTienGoc;

    private BigDecimal soTienGiam;

    private BigDecimal tongTienThanhToan;

    @Size(max = 255, message = "Ten khach hang khong duoc qua 255 ky tu")
    private String tenKhachHang;

    @Size(max = 500, message = "Dia chi khach hang khong duoc qua 500 ky tu")
    private String diaChiKhachHang;

    @Size(max = 20, message = "So dien thoai khong duoc qua 20 ky tu")
    private String soDienThoai;

    private String trangThai;

    private String ghiChu;

    private LocalDateTime ngayNhanHang;

    private LocalDateTime ngayThanhToan;
}