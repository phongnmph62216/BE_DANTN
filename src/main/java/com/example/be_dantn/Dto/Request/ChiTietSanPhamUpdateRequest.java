package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChiTietSanPhamUpdateRequest {

    @NotNull(message = "Giá nhập không được để trống")
    @Min(value = 0, message = "Giá nhập phải lớn hơn hoặc bằng 0")
    private BigDecimal giaNhap;

    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 0, message = "Giá bán phải lớn hơn hoặc bằng 0")
    private BigDecimal giaBan;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0")
    private Integer soLuongTon;

    private String anh; // URL ảnh mới (nếu có thay đổi)

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;
}
