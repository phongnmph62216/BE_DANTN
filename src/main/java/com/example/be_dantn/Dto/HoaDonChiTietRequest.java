package com.example.be_dantn.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonChiTietRequest {

    @NotNull(message = "Id hoa don khong duoc de trong")
    private Long idHoaDon;

    @NotNull(message = "Id chi tiet san pham khong duoc de trong")
    private Long idChiTietSanPham;

    @NotNull(message = "So luong khong duoc de trong")
    @Positive(message = "So luong phai lon hon 0")
    private Integer soLuong;

    private Long nguoiTao;

    private Long nguoiSua;
}