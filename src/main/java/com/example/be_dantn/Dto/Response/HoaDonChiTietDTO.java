package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class HoaDonChiTietDTO {
    private Long idChiTietSanPham;
    private String maChiTietSanPham;
    private String tenSanPham;
    private String anhSanPham;
    private String tenKichCo;
    private String tenMauSac;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal giaGoc;
    private BigDecimal thanhTien;

    public HoaDonChiTietDTO(Long idChiTietSanPham, String maChiTietSanPham, String tenSanPham, String anhSanPham, String tenKichCo, String tenMauSac, Integer soLuong, BigDecimal donGia, BigDecimal giaGoc, BigDecimal thanhTien) {
        this.idChiTietSanPham = idChiTietSanPham;
        this.maChiTietSanPham = maChiTietSanPham;
        this.tenSanPham = tenSanPham;
        this.anhSanPham = anhSanPham;
        this.tenKichCo = tenKichCo;
        this.tenMauSac = tenMauSac;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giaGoc = giaGoc != null ? giaGoc : donGia;
        this.thanhTien = thanhTien;
    }

    public HoaDonChiTietDTO(Long idChiTietSanPham, String maChiTietSanPham, String tenSanPham, String anhSanPham, String tenKichCo, String tenMauSac, Integer soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        this.idChiTietSanPham = idChiTietSanPham;
        this.maChiTietSanPham = maChiTietSanPham;
        this.tenSanPham = tenSanPham;
        this.anhSanPham = anhSanPham;
        this.tenKichCo = tenKichCo;
        this.tenMauSac = tenMauSac;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giaGoc = donGia;
        this.thanhTien = thanhTien;
    }
}
