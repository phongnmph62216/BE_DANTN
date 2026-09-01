package com.example.be_dantn.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHangChoResponseDTO {
    private Long id;
    private String maHoaDon;
    private String tenKhachHang;
    private String soDienThoai;
    private KhachHangDTO khachHang;
    private List<ChiTietDTO> chiTietList;
    private Integer loaiHoaDon;
    private Integer trangThai;
    private BigDecimal phiVanChuyen;
    private String diaChiGiao;
    private String tinhThanhPho;
    private String quanHuyen;
    private String phuongXa;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String maPhieuGiamGia;
    private BigDecimal soTienGoc;
    private BigDecimal soTienGiam;
    private BigDecimal tongTienThanhToan;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KhachHangDTO {
        private Long id;
        private String hoTen;
        private String sdt;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChiTietDTO {
        private Long id;
        private ChiTietSanPhamDTO chiTietSanPham;
        private BigDecimal donGia;
        private Integer soLuong;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChiTietSanPhamDTO {
        private Long id;
        private String maChiTietSanPham;
        private String tenSanPham;
        private String tenMauSac;
        private String tenKichThuoc;
        private Integer soLuongTon;
        private String anh;
    }
}
