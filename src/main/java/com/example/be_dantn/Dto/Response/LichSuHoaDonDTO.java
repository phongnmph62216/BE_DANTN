package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonDTO {
    private Integer trangThai;
    private LocalDateTime thoiGian;
    private String ghiChu;
}
