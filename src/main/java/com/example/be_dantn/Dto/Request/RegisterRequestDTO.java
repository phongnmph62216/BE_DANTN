package com.example.be_dantn.Dto.Request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequestDTO {
    private String ho;
    private String ten;
    private String sdt;
    private Integer gioiTinh;
    private String email;
    private LocalDate ngaySinh;
    private String matKhau;
}
