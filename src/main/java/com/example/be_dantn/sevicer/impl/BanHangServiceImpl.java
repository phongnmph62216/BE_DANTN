package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.DonHangChoResponseDTO;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Dto.Request.ThongTinNhanHangRequestDTO;
import com.example.be_dantn.Entity.*;
import com.example.be_dantn.exception.BadRequestException;
import com.example.be_dantn.Repository.*;
import com.example.be_dantn.sevicer.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BanHangServiceImpl implements BanHangService {

    @Autowired private com.example.be_dantn.repository.HoaDonRepository hoaDonRepository;
    @Autowired private NhanVienRepository nhanVienRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private PhieuGiamGiaRepository phieuGiamGiaRepository;
    @Autowired private com.example.be_dantn.repository.ThanhToanRepository thanhToanRepository;
    @Autowired private com.example.be_dantn.repository.LichSuHoaDonRepository lichSuHoaDonRepository;

    @Override
    @Transactional
    public HoaDonResponseDTO taoDonHangCho() {
        // Trong thực tế, sẽ lấy nhân viên từ context security
        NhanVien nguoiTao = nhanVienRepository.findById(1L).orElse(null);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon("HD" + System.currentTimeMillis());
        hoaDon.setTrangThai(0); // Trạng thái "Chờ thanh toán"
        hoaDon.setLoaiHoaDon(0); // Mặc định là "Tại quầy"
        hoaDon.setNhanVien(nguoiTao);
        hoaDon.setNgayTao(LocalDateTime.now());

        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        return new HoaDonResponseDTO(
                savedHoaDon.getId(),
                savedHoaDon.getMaHoaDon(),
                nguoiTao != null ? nguoiTao.getHoVaTen() : null,
                null,
                savedHoaDon.getNgayTao(),
                BigDecimal.ZERO,
                savedHoaDon.getLoaiHoaDon(),
                null,
                savedHoaDon.getTrangThai()
        );
    }

    @Override
    @Transactional
    public void capNhatThongTinNhanHang(Long idHoaDon, ThongTinNhanHangRequestDTO request) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + idHoaDon));

        // Cập nhật khách hàng
        if (request.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang()).orElse(null);
            hoaDon.setKhachHang(khachHang);
            if (khachHang != null) {
                hoaDon.setTenKhachHang(khachHang.getHoTen());
                hoaDon.setSoDienThoai(khachHang.getSdt());
            }
        } else {
            hoaDon.setKhachHang(null);
            hoaDon.setTenKhachHang("Khách lẻ");
        }

        // Cập nhật hình thức nhận hàng
        if (Boolean.TRUE.equals(request.getIsGiaoHang())) {
            hoaDon.setLoaiHoaDon(1); // Giao hàng
            hoaDon.setTenKhachHang(request.getTenNguoiNhan());
            hoaDon.setSoDienThoai(request.getSdtNguoiNhan());
            hoaDon.setDiaChiKhachHang(request.getDiaChiChiTiet());
            hoaDon.setPhiVanChuyen(request.getPhiVanChuyen());
        } else {
            hoaDon.setLoaiHoaDon(0); // Tại quầy
            hoaDon.setDiaChiKhachHang(null);
            hoaDon.setPhiVanChuyen(BigDecimal.ZERO);
        }
        
        // Cần có logic tính lại tổng tiền ở đây (tạm bỏ qua theo yêu cầu)
        // hoaDon.setTongTienThanhToan(...)

        hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long thanhToanHoaDon(Long idHoaDon, ThanhToanRequestDTO request) {
        // === BƯỚC 1: VALIDATE HÓA ĐƠN VÀ THANH TOÁN ===
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + idHoaDon));

        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }
        if (hoaDon.getDanhSachChiTiet() == null || hoaDon.getDanhSachChiTiet().isEmpty()) {
            throw new BadRequestException("Hóa đơn chưa có sản phẩm nào.");
        }
        if (hoaDon.getTongTienThanhToan() == null || hoaDon.getTongTienThanhToan().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Tổng tiền hóa đơn phải lớn hơn 0.");
        }

        BigDecimal tienMat = (request.getTienMat() == null) ? BigDecimal.ZERO : request.getTienMat();
        BigDecimal tienChuyenKhoan = (request.getTienChuyenKhoan() == null) ? BigDecimal.ZERO : request.getTienChuyenKhoan();
        BigDecimal tongTienKhachTra = tienMat.add(tienChuyenKhoan);

        if (tongTienKhachTra.compareTo(hoaDon.getTongTienThanhToan()) < 0) {
            throw new BadRequestException("Số tiền khách trả không đủ để thanh toán hóa đơn.");
        }

        // === BƯỚC 2: GHI NHẬN DÒNG TIỀN (BẢNG THANH_TOAN) ===
        NhanVien nguoiThucHien = nhanVienRepository.findById(1L).orElse(null); // Lấy NV từ context
        
        if (tienMat.compareTo(BigDecimal.ZERO) > 0) {
            ThanhToan ttTienMat = ThanhToan.builder()
                    .hoaDon(hoaDon)
                    .phuongThuc("1") // 1: Tiền mặt
                    .soTien(tienMat)
                    .thoiGian(LocalDateTime.now())
                    .nguoiThucHien(nguoiThucHien != null ? nguoiThucHien.getHoVaTen() : "Hệ thống")
                    .ghiChu(request.getGhiChu())
                    .build();
            thanhToanRepository.save(ttTienMat);
        }
        if (tienChuyenKhoan.compareTo(BigDecimal.ZERO) > 0) {
            ThanhToan ttCK = ThanhToan.builder()
                    .hoaDon(hoaDon)
                    .phuongThuc("2") // 2: Chuyển khoản
                    .soTien(tienChuyenKhoan)
                    .thoiGian(LocalDateTime.now())
                    .nguoiThucHien(nguoiThucHien != null ? nguoiThucHien.getHoVaTen() : "Hệ thống")
                    .ghiChu(request.getGhiChu())
                    .build();
            thanhToanRepository.save(ttCK);
        }

        // === BƯỚC 3: CHUYỂN TRẠNG THÁI HÓA ĐƠN & TRỪ MÃ GIẢM GIÁ ===
        if (hoaDon.getPhieuGiamGia() != null) {
            PhieuGiamGia pgg = hoaDon.getPhieuGiamGia();
            if (pgg.getSoLuong() > 0) {
                pgg.setSoLuong(pgg.getSoLuong() - 1);
                phieuGiamGiaRepository.save(pgg);
            }
        }

        if (hoaDon.getLoaiHoaDon() == 0) { // Tại quầy
            hoaDon.setTrangThai(4); // Đã hoàn thành
        } else { // Giao hàng
            hoaDon.setTrangThai(2); // Chờ giao
        }
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setNguoiSua(nguoiThucHien != null ? nguoiThucHien.getHoVaTen() : "Hệ thống");
        hoaDonRepository.save(hoaDon);

        // === BƯỚC 4: GHI LỊCH SỬ THAO TÁC ===
        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .hoaDon(hoaDon)
                .nhanVien(nguoiThucHien)
                .trangThai(hoaDon.getTrangThai())
                .hanhDong("Xác nhận đặt hàng và thanh toán thành công")
                .ghiChu("Khách trả: " + tongTienKhachTra)
                .build();
        lichSuHoaDonRepository.save(lichSu);

        return hoaDon.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHangChoResponseDTO> layDanhSachDonHangCho() {
        List<HoaDon> list = hoaDonRepository.findByTrangThaiOrderByNgayTaoDesc(0);
        return list.stream().map(h -> {
            DonHangChoResponseDTO.KhachHangDTO kh = null;
            if (h.getKhachHang() != null) {
                kh = DonHangChoResponseDTO.KhachHangDTO.builder()
                        .id(h.getKhachHang().getId())
                        .hoTen(h.getKhachHang().getHoTen())
                        .sdt(h.getKhachHang().getSdt())
                        .email(h.getKhachHang().getEmail())
                        .build();
            }

            List<DonHangChoResponseDTO.ChiTietDTO> chiTietList = null;
            if (h.getDanhSachChiTiet() != null) {
                chiTietList = h.getDanhSachChiTiet().stream().map(ct -> {
                    ChiTietSanPham ctsp = ct.getChiTietSanPham();
                    DonHangChoResponseDTO.ChiTietSanPhamDTO ctspDto = null;
                    if (ctsp != null) {
                        ctspDto = DonHangChoResponseDTO.ChiTietSanPhamDTO.builder()
                                .id(ctsp.getId())
                                .maChiTietSanPham(ctsp.getMaChiTietSanPham())
                                .tenSanPham(ctsp.getSanPham() != null ? ctsp.getSanPham().getTenSanPham() : "")
                                .tenMauSac(ctsp.getMauSac() != null ? ctsp.getMauSac().getTenMauSac() : "")
                                .tenKichThuoc(ctsp.getKichThuoc() != null ? ctsp.getKichThuoc().getTenKichThuoc() : "")
                                .soLuongTon(ctsp.getSoLuongTon())
                                .anh(ctsp.getAnh())
                                .build();
                    }
                    return DonHangChoResponseDTO.ChiTietDTO.builder()
                            .id(ct.getId())
                            .chiTietSanPham(ctspDto)
                            .donGia(ct.getDonGia())
                            .soLuong(ct.getSoLuong())
                            .build();
                }).collect(Collectors.toList());
            }

            return DonHangChoResponseDTO.builder()
                    .id(h.getId())
                    .maHoaDon(h.getMaHoaDon())
                    .tenKhachHang(h.getTenKhachHang())
                    .soDienThoai(h.getSoDienThoai())
                    .khachHang(kh)
                    .chiTietList(chiTietList)
                    .loaiHoaDon(h.getLoaiHoaDon())
                    .trangThai(h.getTrangThai())
                    .phiVanChuyen(h.getPhiVanChuyen())
                    .diaChiGiao(h.getDiaChiKhachHang())
                    .tinhThanhPho("")
                    .quanHuyen("")
                    .phuongXa("")
                    .tenNguoiNhan(h.getLoaiHoaDon() == 1 ? h.getTenKhachHang() : "")
                    .sdtNguoiNhan(h.getLoaiHoaDon() == 1 ? h.getSoDienThoai() : "")
                    .maPhieuGiamGia(h.getPhieuGiamGia() != null ? h.getPhieuGiamGia().getMaPhieuGiamGia() : null)
                    .soTienGoc(h.getSoTienGoc())
                    .soTienGiam(h.getSoTienGiam())
                    .tongTienThanhToan(h.getTongTienThanhToan())
                    .build();
        }).collect(Collectors.toList());
    }
}
