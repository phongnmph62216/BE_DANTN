package com.example.be_dantn.Dto.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SanPhamResponse {
    private Long id;
    private String maSanPham;
    private String tenSanPham;
    private String hinhAnh;
    private Integer trangThai;
    private String tenThuongHieu;
    private String tenChatLieu;
    private Long tongTonKho;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;

    public SanPhamResponse(Long id, String maSanPham, String tenSanPham, String hinhAnh, Integer trangThai, String tenThuongHieu, String tenChatLieu, Long tongTonKho, BigDecimal giaThapNhat, BigDecimal giaCaoNhat) {
        this.id = id;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.hinhAnh = hinhAnh;
        this.trangThai = trangThai;
        this.tenThuongHieu = tenThuongHieu;
        this.tenChatLieu = tenChatLieu;
        this.tongTonKho = tongTonKho != null ? tongTonKho : 0L;
        this.giaThapNhat = giaThapNhat;
        this.giaCaoNhat = giaCaoNhat;
    }
}
