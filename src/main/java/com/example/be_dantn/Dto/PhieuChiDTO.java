package com.example.be_dantn.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhieuChiDTO {
    private Long id;
    private Long idGiaoCa;
    private String maPhieu;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngayTao;
    private BigDecimal soTien;
    private String lyDo;
    private String nguoiTao;
}
