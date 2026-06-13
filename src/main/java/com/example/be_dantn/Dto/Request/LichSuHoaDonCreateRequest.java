package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LichSuHoaDonCreateRequest {

    @NotNull(message = "ID hóa đơn không được để trống")
    private Long idHoaDon;

    private Long idNhanVien;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    private String hanhDong;

    private String ghiChu;

    private String nguoiThucHien;
}