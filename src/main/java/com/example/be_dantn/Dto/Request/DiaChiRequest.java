package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiaChiRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String sdtNguoiNhan;

    @NotBlank(message = "Địa chỉ cụ thể không được để trống")
    private String diaChiCuThe;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String tinhThanhPho;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String quanHuyen;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String phuongXa;

    private Boolean kieuDiaChiLaMacDinh = false;
}
