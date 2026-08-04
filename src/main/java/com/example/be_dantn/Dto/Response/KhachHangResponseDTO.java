package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangResponseDTO {
    private Long id;
    private String maKhachHang;
    private String hoTen;
    private String sdt;
    private String email;
    private Integer gioiTinh;
    private Integer trangThai;
    private String diaChiMacDinh;
    private LocalDate ngaySinh;
    private Long tongDon;
    private BigDecimal tongChiTieu;
    private LocalDateTime lanMuaGanNhat;

    public KhachHangResponseDTO(Long id, String maKhachHang, String hoTen, String sdt, String email, Integer gioiTinh, Integer trangThai, String diaChiMacDinh, LocalDate ngaySinh) {
        this.id = id;
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.trangThai = trangThai;
        this.diaChiMacDinh = diaChiMacDinh;
        this.ngaySinh = ngaySinh;
        this.tongDon = 0L;
        this.tongChiTieu = BigDecimal.ZERO;
        this.lanMuaGanNhat = null;
    }
}

