package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class KhachHangRequest {

    private String maKhachHang;

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String sdt;

    @Email(message = "Email không hợp lệ")
    private String email;

    private Integer gioiTinh;
    private LocalDate ngaySinh;
    private Integer trangThai;
}
