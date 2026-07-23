package com.example.be_dantn.Dto.Response;

import com.example.be_dantn.Entity.SanPham;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDetailResponse {
    private Long id;
    private String maSanPham;
    private String tenSanPham;
    private String moTa;
    private String hinhAnh;
    private Integer trangThai;

    // 8 ID thuộc tính
    private Long idThuongHieu;
    private Long idChatLieu;
    private Long idXuatSu;
    private Long idKieuDang;
    private Long idLoaiSanPham;
    private Long idCoAo;
    private Long idTayAo;
    private Long idVaiAo;

    public static SanPhamDetailResponse fromEntity(SanPham sanPham) {
        return SanPhamDetailResponse.builder()
                .id(sanPham.getId())
                .maSanPham(sanPham.getMaSanPham())
                .tenSanPham(sanPham.getTenSanPham())
                .moTa(sanPham.getMoTa())
                .hinhAnh(sanPham.getHinhAnh())
                .trangThai(sanPham.getTrangThai())
                .idThuongHieu(sanPham.getThuongHieu() != null ? sanPham.getThuongHieu().getId() : null)
                .idChatLieu(sanPham.getChatLieu() != null ? sanPham.getChatLieu().getId() : null)
                .idXuatSu(sanPham.getXuatSu() != null ? sanPham.getXuatSu().getId() : null)
                .idKieuDang(sanPham.getKieuDang() != null ? sanPham.getKieuDang().getId() : null)
                .idLoaiSanPham(sanPham.getLoaiSanPham() != null ? sanPham.getLoaiSanPham().getId() : null)
                .idCoAo(sanPham.getCoAo() != null ? sanPham.getCoAo().getId() : null)
                .idTayAo(sanPham.getTayAo() != null ? sanPham.getTayAo().getId() : null)
                .idVaiAo(sanPham.getVaiAo() != null ? sanPham.getVaiAo().getId() : null)
                .build();
    }
}
