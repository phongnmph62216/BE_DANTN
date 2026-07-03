package com.example.be_dantn.Dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiaoCaStatusDTO {
    private boolean hasScheduleToday;
    private Long scheduleId;
    private String shiftName;
    private String employeeName;
    private String status; // "NOT_OPENED", "ACTIVE", "CLOSED", "NO_SCHEDULE"
    private BigDecimal previousShiftCash;
    private BigDecimal previousShiftBank;
    private Long giaoCaId;
    private BigDecimal tienMatThuTrongCa;
    private BigDecimal tienChuyenKhoanTrongCa;
    private java.time.LocalDate ngayLamViec;
    private java.time.LocalTime gioBatDau;
    private java.time.LocalTime gioKetThuc;

    private BigDecimal tienMatChiRa;
}
