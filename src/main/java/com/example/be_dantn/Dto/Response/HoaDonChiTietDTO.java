package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietDTO {
    private Long idChiTietSanPham;
    private String tenSanPham;
    private String anhSanPham;
    private String tenKichCo;
    private String tenMauSac;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}
