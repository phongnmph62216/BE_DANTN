package com.example.be_dantn.Dto;

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
public class HoaDonResponseDTO {
    private Long id;
    private String maHoaDon;
    private String tenNhanVien;
    private String tenKhachHang;
    private LocalDateTime ngayTao;
    private BigDecimal tongTien; // tong_tien_thanh_toan
    private Integer loaiDon;
    private String sdtKhachHang;
    private Integer trangThai;
}
