package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuGiamGiaResponseDTO {
    private Long id;
    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
    private Integer loaiGiam;
    private BigDecimal giaTri;
    private BigDecimal giaGiamToiDa;
    private BigDecimal dieuKienGiam;
    private Integer soLuong;
    private Integer soLuongDaDung; // Sẽ được tính toán sau
    private Integer kieuApDung;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai;
    private List<Long> danhSachKhachHangIds; // Thêm mới
}
