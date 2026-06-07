package com.example.be_dantn.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhanVienrequest {

    private Long id;

    private String maNhanVien;
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100,
            message = "Họ và tên phải từ 2 đến 100 ký tự")
    @Pattern(
            regexp = "^[\\p{L}\\s]+$",
            message = "Họ và tên chỉ được chứa chữ cái và khoảng trắng"
    )
    private String hoVaTen;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String soDienThoai;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100,
            message = "Email tối đa 100 ký tự")
    private String email;

    private String matKhau;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(
            regexp = "^\\d{12}$",
            message = "CCCD phải gồm đúng 12 chữ số"
    )
    private String cccd;

    private Integer gioiTinh;

    private LocalDate ngaySinh;

    private String diaChi;

    private String vaiTro;

    private Integer trangThai;

    private LocalDate ngayVaoLam;

    private String anhDaiDien;
}
