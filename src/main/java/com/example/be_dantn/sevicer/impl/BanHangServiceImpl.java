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
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class BanHangServiceImpl implements BanHangService {

    @Autowired private com.example.be_dantn.repository.HoaDonRepository hoaDonRepository;
    @Autowired private NhanVienRepository nhanVienRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private PhieuGiamGiaRepository phieuGiamGiaRepository;
    @Autowired private com.example.be_dantn.repository.ThanhToanRepository thanhToanRepository;
    @Autowired private com.example.be_dantn.repository.LichSuHoaDonRepository lichSuHoaDonRepository;
    @Autowired private com.example.be_dantn.repository.HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired private com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    @Transactional
    public HoaDonResponseDTO taoDonHangCho() {
        // Trong thực tế, sẽ lấy nhân viên từ context security
        NhanVien nguoiTao = nhanVienRepository.findById(1L).orElse(null);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(codeGenerator.generateCode("hoa_don", "ma_hoa_don", "HD"));
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
        tinhTienHoaDon(hoaDon);
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
            hoaDon.setTrangThai(1); // Đã xác nhận
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

    @Override
    @Transactional(readOnly = true)
    public Object layChiTietDonHang(Long orderId) {
        HoaDon h = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + orderId));

        Map<String, Object> map = new HashMap<>();
        map.put("id", h.getId());
        map.put("maHoaDon", h.getMaHoaDon());
        map.put("tenKhachHang", h.getTenKhachHang() != null ? h.getTenKhachHang() : "Khách lẻ");
        map.put("soDienThoai", h.getSoDienThoai() != null ? h.getSoDienThoai() : "");
        map.put("loaiHoaDon", h.getLoaiHoaDon());
        map.put("trangThai", h.getTrangThai());
        map.put("phiVanChuyen", h.getPhiVanChuyen() != null ? h.getPhiVanChuyen() : BigDecimal.ZERO);
        map.put("diaChiGiao", h.getDiaChiKhachHang() != null ? h.getDiaChiKhachHang() : "");
        map.put("tinhThanhPho", "");
        map.put("quanHuyen", "");
        map.put("phuongXa", "");
        map.put("tenNguoiNhan", h.getLoaiHoaDon() == 1 ? h.getTenKhachHang() : "");
        map.put("sdtNguoiNhan", h.getLoaiHoaDon() == 1 ? h.getSoDienThoai() : "");
        map.put("maPhieuGiamGia", h.getPhieuGiamGia() != null ? h.getPhieuGiamGia().getMaPhieuGiamGia() : null);
        map.put("tenPhieuGiamGia", h.getPhieuGiamGia() != null ? h.getPhieuGiamGia().getTenPhieuGiamGia() : null);
        map.put("soTienGoc", h.getSoTienGoc() != null ? h.getSoTienGoc() : BigDecimal.ZERO);
        map.put("soTienGiam", h.getSoTienGiam() != null ? h.getSoTienGiam() : BigDecimal.ZERO);
        map.put("tongTien", h.getTongTienThanhToan() != null ? h.getTongTienThanhToan() : BigDecimal.ZERO);
        map.put("tongTienThanhToan", h.getTongTienThanhToan() != null ? h.getTongTienThanhToan() : BigDecimal.ZERO);

        if (h.getKhachHang() != null) {
            Map<String, Object> kh = new HashMap<>();
            kh.put("id", h.getKhachHang().getId());
            kh.put("hoTen", h.getKhachHang().getHoTen());
            kh.put("sdt", h.getKhachHang().getSdt());
            kh.put("email", h.getKhachHang().getEmail());
            map.put("khachHang", kh);
        } else {
            map.put("khachHang", null);
        }

        List<Map<String, Object>> chiTietList = new ArrayList<>();
        if (h.getDanhSachChiTiet() != null) {
            for (HoaDonChiTiet ct : h.getDanhSachChiTiet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("idHoaDonChiTiet", ct.getId());
                ChiTietSanPham ctsp = ct.getChiTietSanPham();
                if (ctsp != null) {
                    item.put("idChiTietSanPham", ctsp.getId());
                    item.put("maChiTietSanPham", ctsp.getMaChiTietSanPham());
                    item.put("tenSanPham", ctsp.getSanPham() != null ? ctsp.getSanPham().getTenSanPham() : "");
                    item.put("tenMauSac", ctsp.getMauSac() != null ? ctsp.getMauSac().getTenMauSac() : "");
                    item.put("tenKichThuoc", ctsp.getKichThuoc() != null ? ctsp.getKichThuoc().getTenKichThuoc() : "");
                    item.put("anhSanPham", ctsp.getAnh() != null ? ctsp.getAnh() : "");
                }
                item.put("donGia", ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO);
                item.put("soLuong", ct.getSoLuong() != null ? ct.getSoLuong() : 0);
                
                BigDecimal donGia = ct.getDonGia() != null ? ct.getDonGia() : BigDecimal.ZERO;
                BigDecimal soLuong = BigDecimal.valueOf(ct.getSoLuong() != null ? ct.getSoLuong() : 0);
                item.put("thanhTien", donGia.multiply(soLuong));
                
                chiTietList.add(item);
            }
        }
        map.put("chiTietDonHang", chiTietList);

        return map;
    }

    @Override
    @Transactional
    public void capNhatKhachHang(Long idHoaDon, Long idKhachHang) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + idHoaDon));
        
        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }

        if (idKhachHang != null) {
            KhachHang kh = khachHangRepository.findById(idKhachHang)
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy khách hàng với ID: " + idKhachHang));
            hoaDon.setKhachHang(kh);
            hoaDon.setTenKhachHang(kh.getHoTen());
            hoaDon.setSoDienThoai(kh.getSdt());
        } else {
            hoaDon.setKhachHang(null);
            hoaDon.setTenKhachHang("Khách lẻ");
            hoaDon.setSoDienThoai("");
        }

        tinhTienHoaDon(hoaDon);
        hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional
    public void themSanPhamVaoHoaDon(Long idHoaDon, Long idChiTietSanPham, Integer soLuong) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + idHoaDon));
        
        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }

        ChiTietSanPham variant = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy biến thể sản phẩm với ID: " + idChiTietSanPham));

        if (variant.getSoLuongTon() < soLuong) {
            throw new BadRequestException("Số lượng tồn kho không đủ (còn lại: " + variant.getSoLuongTon() + ").");
        }

        // Trừ tồn kho biến thể
        variant.setSoLuongTon(variant.getSoLuongTon() - soLuong);
        chiTietSanPhamRepository.save(variant);

        // Tìm xem sản phẩm đã có trong hóa đơn chưa
        HoaDonChiTiet existingDetail = null;
        if (hoaDon.getDanhSachChiTiet() != null) {
            for (HoaDonChiTiet ct : hoaDon.getDanhSachChiTiet()) {
                if (ct.getChiTietSanPham().getId().equals(idChiTietSanPham)) {
                    existingDetail = ct;
                    break;
                }
            }
        }

        if (existingDetail != null) {
            existingDetail.setSoLuong(existingDetail.getSoLuong() + soLuong);
            hoaDonChiTietRepository.save(existingDetail);
        } else {
            BigDecimal donGia = variant.getGiaBan();
            if (variant.getDotGiamGia() != null 
                && variant.getDotGiamGia().getTrangThai() != null 
                && variant.getDotGiamGia().getTrangThai() == 1) {
                Integer phanTram = variant.getDotGiamGia().getPhanTramGiam();
                if (phanTram != null && phanTram > 0) {
                    BigDecimal discount = donGia.multiply(BigDecimal.valueOf(phanTram)).divide(BigDecimal.valueOf(100));
                    donGia = donGia.subtract(discount);
                }
            }

            HoaDonChiTiet newDetail = HoaDonChiTiet.builder()
                    .hoaDon(hoaDon)
                    .chiTietSanPham(variant)
                    .soLuong(soLuong)
                    .donGia(donGia)
                    .build();
            hoaDonChiTietRepository.save(newDetail);

            if (hoaDon.getDanhSachChiTiet() == null) {
                hoaDon.setDanhSachChiTiet(new ArrayList<>());
            }
            hoaDon.getDanhSachChiTiet().add(newDetail);
        }

        tinhTienHoaDon(hoaDon);
        hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional
    public void capNhatSoLuongSanPham(Long idHoaDonChiTiet, Integer soLuong) {
        HoaDonChiTiet detail = hoaDonChiTietRepository.findById(idHoaDonChiTiet)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy chi tiết hóa đơn với ID: " + idHoaDonChiTiet));
        
        HoaDon hoaDon = detail.getHoaDon();
        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }

        ChiTietSanPham variant = detail.getChiTietSanPham();
        int diff = soLuong - detail.getSoLuong();

        if (diff > 0) {
            if (variant.getSoLuongTon() < diff) {
                throw new BadRequestException("Số lượng tồn kho không đủ (còn lại: " + variant.getSoLuongTon() + ").");
            }
            variant.setSoLuongTon(variant.getSoLuongTon() - diff);
        } else if (diff < 0) {
            variant.setSoLuongTon(variant.getSoLuongTon() + Math.abs(diff));
        }

        chiTietSanPhamRepository.save(variant);

        detail.setSoLuong(soLuong);
        hoaDonChiTietRepository.save(detail);

        tinhTienHoaDon(hoaDon);
        hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional
    public void xoaSanPhamKhoiHoaDon(Long idHoaDonChiTiet) {
        HoaDonChiTiet detail = hoaDonChiTietRepository.findById(idHoaDonChiTiet)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy chi tiết hóa đơn với ID: " + idHoaDonChiTiet));
        
        HoaDon hoaDon = detail.getHoaDon();
        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }

        ChiTietSanPham variant = detail.getChiTietSanPham();
        variant.setSoLuongTon(variant.getSoLuongTon() + detail.getSoLuong());
        chiTietSanPhamRepository.save(variant);

        hoaDon.getDanhSachChiTiet().remove(detail);
        hoaDonChiTietRepository.delete(detail);

        tinhTienHoaDon(hoaDon);
        hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional
    public void apDungVoucher(Long idHoaDon, String maPhieuGiamGia) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + idHoaDon));
        
        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }

        PhieuGiamGia voucher = phieuGiamGiaRepository.findByMaPhieuGiamGia(maPhieuGiamGia)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy voucher với mã: " + maPhieuGiamGia));

        // Kiểm tra trạng thái voucher
        if (voucher.getTrangThai() != 1) {
            throw new BadRequestException("Voucher không hoạt động.");
        }
        if (voucher.getSoLuong() <= 0) {
            throw new BadRequestException("Voucher đã hết số lượng sử dụng.");
        }
        if (voucher.getNgayBatDau().isAfter(LocalDateTime.now()) || voucher.getNgayKetThuc().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Voucher đã hết hạn sử dụng.");
        }

        // Kiểm tra điều kiện giảm giá tối thiểu
        BigDecimal soTienGoc = BigDecimal.ZERO;
        if (hoaDon.getDanhSachChiTiet() != null) {
            for (HoaDonChiTiet ct : hoaDon.getDanhSachChiTiet()) {
                soTienGoc = soTienGoc.add(ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())));
            }
        }

        BigDecimal dieuKien = voucher.getDieuKienGiam() != null ? voucher.getDieuKienGiam() : BigDecimal.ZERO;
        if (soTienGoc.compareTo(dieuKien) < 0) {
            throw new BadRequestException("Giá trị đơn hàng chưa đạt điều kiện tối thiểu: " + dieuKien);
        }

        hoaDon.setPhieuGiamGia(voucher);
        tinhTienHoaDon(hoaDon);
        hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional
    public void xoaVoucher(Long idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + idHoaDon));
        
        if (hoaDon.getTrangThai() != 0) {
            throw new BadRequestException("Hóa đơn không ở trạng thái chờ thanh toán.");
        }

        hoaDon.setPhieuGiamGia(null);
        tinhTienHoaDon(hoaDon);
        hoaDonRepository.save(hoaDon);
    }

    private void tinhTienHoaDon(HoaDon hoaDon) {
        BigDecimal soTienGoc = BigDecimal.ZERO;
        if (hoaDon.getDanhSachChiTiet() != null) {
            for (HoaDonChiTiet ct : hoaDon.getDanhSachChiTiet()) {
                BigDecimal soLuong = BigDecimal.valueOf(ct.getSoLuong());
                soTienGoc = soTienGoc.add(ct.getDonGia().multiply(soLuong));
            }
        }
        hoaDon.setSoTienGoc(soTienGoc);

        BigDecimal soTienGiam = BigDecimal.ZERO;
        PhieuGiamGia pgg = hoaDon.getPhieuGiamGia();
        if (pgg != null) {
            BigDecimal dieuKien = pgg.getDieuKienGiam() != null ? pgg.getDieuKienGiam() : BigDecimal.ZERO;
            if (soTienGoc.compareTo(dieuKien) >= 0) {
                if (pgg.getLoaiGiam() == 0) { // Giảm %
                    BigDecimal phanTram = pgg.getGiaTri().divide(BigDecimal.valueOf(100));
                    soTienGiam = soTienGoc.multiply(phanTram);
                    if (pgg.getGiaGiamToiDa() != null && soTienGiam.compareTo(pgg.getGiaGiamToiDa()) > 0) {
                        soTienGiam = pgg.getGiaGiamToiDa();
                    }
                } else { // Giảm tiền mặt
                    soTienGiam = pgg.getGiaTri();
                }
            } else {
                hoaDon.setPhieuGiamGia(null);
            }
        }

        if (soTienGiam.compareTo(soTienGoc) > 0) {
            soTienGiam = soTienGoc;
        }
        hoaDon.setSoTienGiam(soTienGiam);

        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO;
        BigDecimal tongTien = soTienGoc.subtract(soTienGiam).add(phiVanChuyen);
        if (tongTien.compareTo(BigDecimal.ZERO) < 0) {
            tongTien = BigDecimal.ZERO;
        }
        hoaDon.setTongTienThanhToan(tongTien);
    }
}
