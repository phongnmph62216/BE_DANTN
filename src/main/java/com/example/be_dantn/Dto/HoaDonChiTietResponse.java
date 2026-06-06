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
public class HoaDonChiTietResponse {

    private Long id;

    private Long idHoaDon;

    private Long idChiTietSanPham;

    private String maChiTietSanPham;

    private BigDecimal donGia;

    private Integer soLuong;

    private BigDecimal thanhTien;

    private LocalDateTime ngaySua;

    private LocalDateTime ngayTao;

    private Long nguoiTao;

    private Long nguoiSua;

    private String trangThai;
}