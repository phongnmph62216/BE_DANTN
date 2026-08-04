package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChiTietSanPhamCreateRequest {

    @NotNull(message = "ID sản phẩm không được để trống")
    private Long idSanPham;

    @NotNull(message = "Màu sắc không được để trống")
    private Long idMauSac;

    @NotNull(message = "Kích thước không được để trống")
    private Long idKichThuoc;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0")
    private Integer soLuongTon;

    @NotNull(message = "Giá nhập không được để trống")
    @Min(value = 0, message = "Giá nhập phải lớn hơn hoặc bằng 0")
    private BigDecimal giaNhap;

    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán phải lớn hơn hoặc bằng 0")
    private BigDecimal giaBan;

    private String anh;
}
