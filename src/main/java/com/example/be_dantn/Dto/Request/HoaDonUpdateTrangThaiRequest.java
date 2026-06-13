package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HoaDonUpdateTrangThaiRequest {

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    private String hanhDong;

    private String ghiChu;

    private Long idNhanVien;
}