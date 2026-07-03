package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NhanVienCreateRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String hoVaTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @NotBlank(message = "CCCD không được để trống")
    private String cccd;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;

    @NotNull(message = "Giới tính không được để trống")
    private Integer gioiTinh;

    @NotNull(message = "Vai trò không được để trống")
    private Long idVaiTro;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    private String anh;
}
