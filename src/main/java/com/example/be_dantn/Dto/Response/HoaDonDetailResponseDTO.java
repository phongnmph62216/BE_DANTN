package com.example.be_dantn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonDetailResponseDTO {
    // Thông tin cơ bản
    private Long id;
    private String maHoaDon;
    private LocalDateTime ngayTao;
    private String nguoiTao;
    private LocalDateTime ngaySua;
    private String nguoiSua;
    private Integer trangThai;
    private Integer loaiDon;
    private String ghiChu;

    // Thông tin khách hàng
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String diaChi;

    // Thông tin tài chính
    private BigDecimal tongTienHang;
    private BigDecimal giamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;

    // Thông tin phiếu giảm giá
    private Long idPhieuGiamGia;
    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;

    // Các danh sách liên quan
    private List<HoaDonChiTietDTO> danhSachSanPham;
    private List<ThanhToanDTO> lichSuThanhToan;
    private List<LichSuHoaDonDTO> timelineTrangThai;
}
