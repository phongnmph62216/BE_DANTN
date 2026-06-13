package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietResponseDTO {

    private Long id;

    private Long idHoaDon;

    private Long idChiTietSanPham;

    private String maChiTietSanPham;

    private String maSanPham;

    private String tenSanPham;

    private String tenMauSac;

    private String tenKichThuoc;

    private String hinhAnh;

    private BigDecimal donGia;

    private Integer soLuong;

    private BigDecimal thanhTien;

    private Integer trangThai;
}