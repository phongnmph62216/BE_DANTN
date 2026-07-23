package com.example.be_dantn.Dto.Request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ThanhToanRequestDTO {
    private BigDecimal tienMat;
    private BigDecimal tienChuyenKhoan;
    private String ghiChu;
}
