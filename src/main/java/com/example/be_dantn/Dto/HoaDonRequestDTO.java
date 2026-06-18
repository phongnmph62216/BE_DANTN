package com.example.be_dantn.Dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class HoaDonRequestDTO {
    private String maHoaDon;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime tuNgay;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime denNgay;

    private Integer loaiDon;
    private Integer trangThai;
    private int page = 0;
    private int size = 10;
}
