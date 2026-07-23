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
@ToString
public class GiaoCaDTO {

    private Long id;

    private Long idLichLamViec;

    private Long idCaLamViec;

    // Derived from LichLamViec -> NhanVien & CaLamViec
    private String maNhanVienNhanCa;
    private String tenNhanVienNhanCa;
    private String tenCa;

    private Long idNguoiDongCa;

    // Derived from NhanVien (nguoiDongCa)
    private String maNguoiDongCa;
    private String tenNguoiDongCa;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime thoiGianMoCa;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime thoiGianDongCa;

    private BigDecimal tienMatDauCa;

    private BigDecimal tienMatThuTrongCa;

    private BigDecimal tienChuyenKhoanTrongCa;

    private BigDecimal tienMatThucTeChotCa;

    private BigDecimal tienChenhLech;

    private Integer trangThai;

    private BigDecimal tienGiaoCaSau;

    private Integer trangThaiDoiSoat;

    private String phuongAnXuLy;

    private String ghiChuDoiSoat;

    private String ghiChu;

    private BigDecimal tienMatChiRa;
}
