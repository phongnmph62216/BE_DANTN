package com.example.be_dantn.Dto.Request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreatePhieuChiRequest {
    private Long idGiaoCa;
    private BigDecimal soTien;
    private String lyDo;
    private String nguoiTao;
}
