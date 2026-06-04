package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSanPhamResponseDTO {
    private Long id;
    private String anh;
    private String maSanPham;
    private String maChiTietSanPham;
    private String tenKichCo;
    private String tenMauSac;
    private Integer soLuongTon;
    private BigDecimal giaNhap;
    private BigDecimal giaBan;
    private Integer trangThai;
}
