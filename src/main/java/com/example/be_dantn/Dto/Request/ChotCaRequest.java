package com.example.be_dantn.Dto.Request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ChotCaRequest {
    private Long idGiaoCa;
    private BigDecimal tienMatThucTeChotCa;
    private BigDecimal tienChuyenKhoanTrongCa;
    private BigDecimal tienChenhLech;
    private BigDecimal tienGiaoCaSau;
    private Long idNhanVienNhanCa;
    private String matKhauXacNhan;
    private String ghiChu;
}
