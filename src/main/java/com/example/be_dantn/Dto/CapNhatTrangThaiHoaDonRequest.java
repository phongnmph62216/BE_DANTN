package com.example.be_dantn.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CapNhatTrangThaiHoaDonRequest {

    @NotBlank(message = "Trang thai khong duoc de trong")
    private String trangThai;

    private String ghiChu;
}