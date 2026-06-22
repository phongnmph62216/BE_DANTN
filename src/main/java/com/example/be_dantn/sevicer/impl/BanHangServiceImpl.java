package com.example.be_dantn.sevicer.impl;

import jakarta.servlet.http.HttpServletRequest;
import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.DonHangChoResponseDTO;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Dto.Request.ThongTinNhanHangRequestDTO;
import com.example.be_dantn.Entity.*;
import com.example.be_dantn.exception.BadRequestException;
import com.example.be_dantn.Repository.*;
import com.example.be_dantn.sevicer.BanHangService;
import com.example.be_dantn.sevicer.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.text.DecimalFormat;
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
    @Autowired private HttpServletRequest httpServletRequest;
    @Autowired private EmailService emailService;
    @Autowired private ThongBaoRepository thongBaoRepository;

    private NhanVien getLoggedInEmployee() {
        if (httpServletRequest != null) {
            String empIdStr = httpServletRequest.getHeader("X-Employee-Id");
            if (empIdStr != null && !empIdStr.trim().isEmpty()) {
                try {
                    Long empId = Long.parseLong(empIdStr.trim());
                    return nhanVienRepository.findById(empId).orElse(null);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public HoaDonResponseDTO taoDonHangCho(Integer loaiHoaDon) {
        NhanVien nguoiTao = null;
        if (loaiHoaDon == null || loaiHoaDon != 2) {
            nguoiTao = getLoggedInEmployee();
            if (nguoiTao == null) {
                nguoiTao = nhanVienRepository.findById(1L).orElse(null);
            }
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(codeGenerator.generateCode("hoa_don", "ma_hoa_don", "HD"));
        hoaDon.setTrangThai(0); // Trạng thái "Chờ thanh toán"
        hoaDon.setLoaiHoaDon(loaiHoaDon != null ? loaiHoaDon : 0); // 0-Tại quầy, 1-Giao hàng, 2-Online
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

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            hoaDon.setEmail(request.getEmail().trim());
        } else if (hoaDon.getKhachHang() != null) {
            hoaDon.setEmail(hoaDon.getKhachHang().getEmail());
        }

        // Cập nhật hình thức nhận hàng
        if (Boolean.TRUE.equals(request.getIsGiaoHang())) {
            if (hoaDon.getLoaiHoaDon() != 2) {
                hoaDon.setLoaiHoaDon(1); // Giao hàng
            }
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

        // Đơn giao hàng (1) hoặc online (2) hỗ trợ thanh toán COD (trả sau) với số tiền khách trả bằng 0 lúc chốt đơn
        boolean isCOD = (hoaDon.getLoaiHoaDon() == 1 || hoaDon.getLoaiHoaDon() == 2) && tongTienKhachTra.compareTo(BigDecimal.ZERO) == 0;
        if (!isCOD && tongTienKhachTra.compareTo(hoaDon.getTongTienThanhToan()) < 0) {
            throw new BadRequestException("Số tiền khách trả không đủ để thanh toán hóa đơn.");
        }

        // === BƯỚC 2: GHI NHẬN DÒNG TIỀN (BẢNG THANH_TOAN) ===
        NhanVien nguoiThucHien = getLoggedInEmployee();
        if (nguoiThucHien == null && (hoaDon.getLoaiHoaDon() == null || hoaDon.getLoaiHoaDon() != 2)) {
            nguoiThucHien = nhanVienRepository.findById(1L).orElse(null);
        }
        
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
        } else if (hoaDon.getLoaiHoaDon() == 2) { // Bán hàng online
            hoaDon.setTrangThai(0); // Chưa xác nhận (chờ admin duyệt)
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

        // === BƯỚC 5: GỬI EMAIL THÔNG BÁO CHO KHÁCH HÀNG (Nếu là đặt hàng online) ===
        if (hoaDon.getLoaiHoaDon() == 2) {
            String customerEmail = hoaDon.getEmail();
            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                customerEmail = hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getEmail() : null;
            }
            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                try {
                    String customerName = hoaDon.getTenKhachHang() != null ? hoaDon.getTenKhachHang() : (hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getHoTen() : "Khách hàng");
                    String orderCode = hoaDon.getMaHoaDon();
                    
                    DecimalFormat df = new DecimalFormat("#,###");
                    String totalAmountStr = df.format(hoaDon.getTongTienThanhToan()) + " đ";
                    
                    String paymentMethodText = (tienChuyenKhoan.compareTo(BigDecimal.ZERO) > 0) 
                            ? "Thanh toán online qua VNPAY" 
                            : "Thanh toán khi nhận hàng (COD)";
                    
                    // Format products HTML table rows
                    StringBuilder productsHtml = new StringBuilder();
                    if (hoaDon.getDanhSachChiTiet() != null) {
                        for (HoaDonChiTiet detailItem : hoaDon.getDanhSachChiTiet()) {
                            ChiTietSanPham ctsp = detailItem.getChiTietSanPham();
                            String productName = ctsp != null && ctsp.getSanPham() != null ? ctsp.getSanPham().getTenSanPham() : "Sản phẩm";
                            String color = ctsp != null && ctsp.getMauSac() != null ? ctsp.getMauSac().getTenMauSac() : "";
                            String size = ctsp != null && ctsp.getKichThuoc() != null ? ctsp.getKichThuoc().getTenKichThuoc() : "";
                            String variantDetails = String.format("%s (%s, %s)", productName, color, size);
                            
                            String priceStr = df.format(detailItem.getDonGia()) + " đ";
                            productsHtml.append("<tr>")
                                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd;\">").append(variantDetails).append("</td>")
                                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd; text-align: center;\">").append(detailItem.getSoLuong()).append("</td>")
                                    .append("<td style=\"padding: 8px; border-bottom: 1px solid #ddd; text-align: right;\">").append(priceStr).append("</td>")
                                    .append("</tr>");
                        }
                    }
                    
                    String trackingLink = "http://localhost:5173/tra-cuu?maHoaDon=" + orderCode + "&email=" + customerEmail;
                    
                    emailService.sendOrderSuccessEmail(
                            customerEmail,
                            customerName,
                            orderCode,
                            totalAmountStr,
                            paymentMethodText,
                            productsHtml.toString(),
                            trackingLink
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return hoaDon.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonHangChoResponseDTO> layDanhSachDonHangCho() {
        // Chỉ lấy hóa đơn chờ (trạng thái = 0) có loại là 0 (Tại quầy) hoặc 1 (Giao hàng)
        List<HoaDon> list = hoaDonRepository.findByTrangThaiAndLoaiHoaDonIn(0, List.of(0, 1));
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

        // Bán hàng online: Không trừ tồn kho khi thêm vào giỏ hàng (chỉ validate)
        // Bán hàng offline: Trừ tồn kho ngay lập tức
        if (hoaDon.getLoaiHoaDon() != 2) {
            variant.setSoLuongTon(variant.getSoLuongTon() - soLuong);
            chiTietSanPhamRepository.save(variant);
        }

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

        if (hoaDon.getLoaiHoaDon() != 2) {
            if (diff > 0) {
                if (variant.getSoLuongTon() < diff) {
                    throw new BadRequestException("Số lượng tồn kho không đủ (còn lại: " + variant.getSoLuongTon() + ").");
                }
                variant.setSoLuongTon(variant.getSoLuongTon() - diff);
            } else if (diff < 0) {
                variant.setSoLuongTon(variant.getSoLuongTon() + Math.abs(diff));
            }
            chiTietSanPhamRepository.save(variant);
        } else {
            // Đơn online: chỉ validate tồn kho khi tăng số lượng
            if (diff > 0 && variant.getSoLuongTon() < diff) {
                throw new BadRequestException("Số lượng tồn kho không đủ (còn lại: " + variant.getSoLuongTon() + ").");
            }
        }

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

        if (hoaDon.getLoaiHoaDon() != 2) {
            ChiTietSanPham variant = detail.getChiTietSanPham();
            variant.setSoLuongTon(variant.getSoLuongTon() + detail.getSoLuong());
            chiTietSanPhamRepository.save(variant);
        }

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

    @Override
    @Transactional
    public void yeuCauHuyDon(Long orderId, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + orderId));

        if (hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 1) {
            throw new BadRequestException("Chỉ có thể yêu cầu hủy đơn khi đơn hàng đang ở trạng thái Chờ xác nhận hoặc Đã xác nhận.");
        }

        hoaDon.setTrangThaiYeuCauHuy(1); // 1: Chờ xác nhận hủy
        hoaDon.setNgaySua(LocalDateTime.now());
        hoaDon.setNguoiSua("Khách hàng");
        hoaDonRepository.save(hoaDon);

        // Ghi lịch sử hóa đơn
        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .hoaDon(hoaDon)
                .nhanVien(null)
                .trangThai(hoaDon.getTrangThai())
                .hanhDong("Khách hàng gửi yêu cầu hủy đơn")
                .ghiChu(ghiChu)
                .build();
        lichSuHoaDonRepository.save(lichSu);

        // Tạo thông báo cho nhân viên
        ThongBao thongBao = ThongBao.builder()
                .tieuDe("Yêu cầu hủy đơn hàng - " + hoaDon.getMaHoaDon())
                .noiDung("Khách hàng " + hoaDon.getTenKhachHang() + " yêu cầu hủy đơn hàng " + hoaDon.getMaHoaDon() + ". Lý do: " + ghiChu)
                .maHoaDon(hoaDon.getMaHoaDon())
                .idHoaDon(hoaDon.getId())
                .trangThai(0) // Chưa đọc
                .build();
        thongBaoRepository.save(thongBao);
    }

    @Override
    @Transactional
    public void pheDuyetHuyDon(Long orderId, Boolean dongY, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + orderId));

        if (hoaDon.getTrangThaiYeuCauHuy() == null || hoaDon.getTrangThaiYeuCauHuy() != 1) {
            throw new BadRequestException("Hóa đơn này không có yêu cầu hủy nào đang chờ duyệt.");
        }

        NhanVien nguoiThucHien = getLoggedInEmployee();
        if (nguoiThucHien == null) {
            nguoiThucHien = nhanVienRepository.findById(1L).orElse(null);
        }

        if (Boolean.TRUE.equals(dongY)) {
            // Đồng ý hủy
            Integer trangThaiCu = hoaDon.getTrangThai();
            hoaDon.setTrangThai(5); // Đã hủy
            hoaDon.setTrangThaiYeuCauHuy(3); // Đã đồng ý hủy
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiSua(nguoiThucHien != null ? nguoiThucHien.getHoVaTen() : "Hệ thống");

            // Hoàn trả số lượng tồn kho (Copy từ logic của HoaDonServiceImpl.java)
            if (hoaDon.getLoaiHoaDon() != null) {
                if (hoaDon.getLoaiHoaDon() == 0 || hoaDon.getLoaiHoaDon() == 1) {
                    if (hoaDon.getDanhSachChiTiet() != null) {
                        for (HoaDonChiTiet chiTiet : hoaDon.getDanhSachChiTiet()) {
                            ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                            if (ctsp != null) {
                                int soLuongBan = chiTiet.getSoLuong() != null ? chiTiet.getSoLuong() : 0;
                                int soLuongTon = ctsp.getSoLuongTon() != null ? ctsp.getSoLuongTon() : 0;
                                ctsp.setSoLuongTon(soLuongTon + soLuongBan);
                                chiTietSanPhamRepository.save(ctsp);
                            }
                        }
                    }
                } else if (hoaDon.getLoaiHoaDon() == 2) {
                    if (trangThaiCu >= 1) {
                        if (hoaDon.getDanhSachChiTiet() != null) {
                            for (HoaDonChiTiet chiTiet : hoaDon.getDanhSachChiTiet()) {
                                ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                                if (ctsp != null) {
                                    int soLuongBan = chiTiet.getSoLuong() != null ? chiTiet.getSoLuong() : 0;
                                    int soLuongTon = ctsp.getSoLuongTon() != null ? ctsp.getSoLuongTon() : 0;
                                    ctsp.setSoLuongTon(soLuongTon + soLuongBan);
                                    chiTietSanPhamRepository.save(ctsp);
                                }
                            }
                        }
                    }
                }
            }

            hoaDonRepository.save(hoaDon);

            // Ghi lịch sử hóa đơn
            LichSuHoaDon lichSu = LichSuHoaDon.builder()
                    .hoaDon(hoaDon)
                    .nhanVien(nguoiThucHien)
                    .trangThai(5)
                    .hanhDong("Đồng ý yêu cầu hủy đơn hàng")
                    .ghiChu(ghiChu)
                    .build();
            lichSuHoaDonRepository.save(lichSu);

        } else {
            // Từ chối hủy
            hoaDon.setTrangThaiYeuCauHuy(2); // Từ chối hủy
            hoaDon.setNgaySua(LocalDateTime.now());
            hoaDon.setNguoiSua(nguoiThucHien != null ? nguoiThucHien.getHoVaTen() : "Hệ thống");
            hoaDonRepository.save(hoaDon);

            // Ghi lịch sử hóa đơn
            LichSuHoaDon lichSu = LichSuHoaDon.builder()
                    .hoaDon(hoaDon)
                    .nhanVien(nguoiThucHien)
                    .trangThai(hoaDon.getTrangThai())
                    .hanhDong("Từ chối yêu cầu hủy đơn hàng")
                    .ghiChu(ghiChu)
                    .build();
            lichSuHoaDonRepository.save(lichSu);
        }
    }
}
