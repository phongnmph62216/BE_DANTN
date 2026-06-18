package com.example.be_dantn.Dto.Request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ThongTinNhanHangRequestDTO {
    private Long idKhachHang;
    private Boolean isGiaoHang;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiChiTiet;
    private BigDecimal phiVanChuyen;
}
