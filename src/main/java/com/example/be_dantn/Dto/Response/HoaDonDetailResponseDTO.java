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
    // Khối 1: Thông tin chung
    private String maHoaDon;
    private LocalDateTime ngayTao;
    private String nhanVienTao;
    private String nhanVienCapNhat;
    private Integer trangThai;

    // Khối 2: Khách hàng & Giao hàng
    private String tenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;
    private String diaChiGiaoHang;
    private Integer loaiDon;
    private String ghiChu;

    // Khối 3: Thống kê tiền
    private BigDecimal tongTienHang;
    private BigDecimal giamGia;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTienThanhToan;

    // Khối 4: Danh sách sản phẩm
    private List<HoaDonChiTietDTO> danhSachSanPham;

    // Khối 5: Lịch sử thanh toán
    private List<ThanhToanDTO> lichSuThanhToan;

    // Khối 6: Timeline trạng thái
    private List<LichSuHoaDonDTO> timelineTrangThai;
}
