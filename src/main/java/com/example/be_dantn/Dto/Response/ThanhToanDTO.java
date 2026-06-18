package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhToanDTO {
    private String phuongThuc;
    private BigDecimal soTien;
    private LocalDateTime thoiGian;
    private String nguoiThucHien;
    private String ghiChu;
}
