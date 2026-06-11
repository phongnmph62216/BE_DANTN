package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DotGiamGiaResponseDTO {
    private Long id;
    private String maDotGiamGia;
    private String tenDotGiamGia;
    private Integer phanTramGiam;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer trangThai;
    private List<Long> danhSachIdChiTietSanPham;

    // Constructor for JPQL query
    public DotGiamGiaResponseDTO(Long id, String maDotGiamGia, String tenDotGiamGia, Integer phanTramGiam, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc, Integer trangThai) {
        this.id = id;
        this.maDotGiamGia = maDotGiamGia;
        this.tenDotGiamGia = tenDotGiamGia;
        this.phanTramGiam = phanTramGiam;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }
}
