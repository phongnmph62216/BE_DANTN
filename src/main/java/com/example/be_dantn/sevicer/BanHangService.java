package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.DonHangChoResponseDTO;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Dto.Request.ThongTinNhanHangRequestDTO;
import java.util.List;

public interface BanHangService {
    HoaDonResponseDTO taoDonHangCho();
    void capNhatThongTinNhanHang(Long idHoaDon, ThongTinNhanHangRequestDTO request);
    Long thanhToanHoaDon(Long idHoaDon, ThanhToanRequestDTO request);
    List<DonHangChoResponseDTO> layDanhSachDonHangCho();
    Object layChiTietDonHang(Long orderId);
    void capNhatKhachHang(Long idHoaDon, Long idKhachHang);
    void themSanPhamVaoHoaDon(Long idHoaDon, Long idChiTietSanPham, Integer soLuong);
    void capNhatSoLuongSanPham(Long idHoaDonChiTiet, Integer soLuong);
    void xoaSanPhamKhoiHoaDon(Long idHoaDonChiTiet);
    void apDungVoucher(Long idHoaDon, String maPhieuGiamGia);
    void xoaVoucher(Long idHoaDon);
}
