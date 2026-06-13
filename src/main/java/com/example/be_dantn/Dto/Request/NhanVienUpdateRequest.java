package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NhanVienUpdateRequest {
    @NotNull(message = "ID vai trò không được để trống")
    private Long idVaiTro;

    @NotBlank(message = "Họ và tên không được để trống")
    private String hoVaTen;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String cccd;
    private Integer gioiTinh;
    private LocalDate ngaySinh;
    private String diaChi;
    private String anh;
}
