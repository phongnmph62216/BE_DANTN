package com.example.be_dantn.sevicer.impl;

import jakarta.servlet.http.HttpServletRequest;
import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.HoaDonChiTietDTO;
import com.example.be_dantn.Dto.Response.HoaDonDetailResponseDTO;
import com.example.be_dantn.Dto.Response.LichSuHoaDonDTO;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.ThanhToanDTO;
import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.Entity.LichSuHoaDon;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Entity.ChiTietSanPham;
import com.example.be_dantn.Entity.HoaDonChiTiet;
import com.example.be_dantn.exception.BadRequestException;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.Repository.ChiTietSanPhamRepository;
import com.example.be_dantn.sevicer.HoaDonService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    @Autowired
    private com.example.be_dantn.repository.HoaDonRepository hoaDonRepository;

    @Autowired
    private com.example.be_dantn.repository.HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private com.example.be_dantn.repository.ThanhToanRepository thanhToanRepository;

    @Autowired
    private com.example.be_dantn.repository.LichSuHoaDonRepository lichSuHoaDonRepository;
    
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

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
    public Page<HoaDonResponseDTO> layDanhSachHoaDon(String maHoaDon, LocalDateTime tuNgay, LocalDateTime denNgay, Integer loaiDon, Integer trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return hoaDonRepository.findHoaDonByFilters(maHoaDon, tuNgay, denNgay, loaiDon, trangThai, pageable);
    }

    @Override
    public byte[] xuatExcelDanhSachHoaDon(String maHoaDon, LocalDateTime tuNgay, LocalDateTime denNgay, Integer loaiDon, Integer trangThai) {
        List<HoaDonResponseDTO> hoaDonList = hoaDonRepository.findHoaDonByFiltersForExport(maHoaDon, tuNgay, denNgay, loaiDon, trangThai);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sách hóa đơn");

            String[] headers = {"STT", "Mã hóa đơn", "Tên nhân viên", "Tên khách hàng", "Ngày tạo", "Tổng tiền", "Loại đơn", "SĐT khách hàng", "Trạng thái"};
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers[col]);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (HoaDonResponseDTO hoaDon : hoaDonList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(hoaDon.getMaHoaDon());
                row.createCell(2).setCellValue(hoaDon.getTenNhanVien());
                row.createCell(3).setCellValue(hoaDon.getTenKhachHang());
                row.createCell(4).setCellValue(hoaDon.getNgayTao().format(formatter));
                row.createCell(5).setCellValue(hoaDon.getTongTien().doubleValue());
                row.createCell(6).setCellValue(getLoaiDonText(hoaDon.getLoaiDon()));
                row.createCell(7).setCellValue(hoaDon.getSdtKhachHang());
                row.createCell(8).setCellValue(getTrangThaiText(hoaDon.getTrangThai()));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonDetailResponseDTO layChiTietHoaDon(Long id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + id));

        List<HoaDonChiTietDTO> danhSachSanPham = hoaDonChiTietRepository.findByIdHoaDon(id);
        List<ThanhToanDTO> lichSuThanhToan = thanhToanRepository.findByIdHoaDon(id);
        List<LichSuHoaDonResponseDTO> timelineTrangThaiFull = lichSuHoaDonRepository.findByIdHoaDon(id);
        
        List<LichSuHoaDonDTO> timelineTrangThai = timelineTrangThaiFull.stream()
            .map(l -> new LichSuHoaDonDTO(l.getTrangThai(), l.getThoiGian(), l.getGhiChu()))
            .collect(Collectors.toList());

        String nguoiTao = (hoaDon.getNhanVien() != null) ? hoaDon.getNhanVien().getHoVaTen() : "";
        String email = hoaDon.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = (hoaDon.getKhachHang() != null) ? hoaDon.getKhachHang().getEmail() : "";
        }
        String diaChi = hoaDon.getDiaChiKhachHang();

        HoaDonDetailResponseDTO.HoaDonDetailResponseDTOBuilder builder = HoaDonDetailResponseDTO.builder()
                .id(hoaDon.getId())
                .maHoaDon(hoaDon.getMaHoaDon())
                .ngayTao(hoaDon.getNgayTao())
                .nguoiTao(nguoiTao)
                .ngaySua(hoaDon.getNgaySua())
                .nguoiSua(hoaDon.getNguoiSua())
                .trangThai(hoaDon.getTrangThai())
                .tenKhachHang(hoaDon.getTenKhachHang())
                .soDienThoai(hoaDon.getSoDienThoai())
                .email(email)
                .diaChi(diaChi)
                .loaiDon(hoaDon.getLoaiHoaDon())
                .trangThaiYeuCauHuy(hoaDon.getTrangThaiYeuCauHuy())
                .ghiChu(hoaDon.getGhiChu())
                .tongTienHang(hoaDon.getSoTienGoc())
                .giamGia(hoaDon.getSoTienGiam())
                .phiVanChuyen(hoaDon.getPhiVanChuyen())
                .tongTien(hoaDon.getTongTienThanhToan())
                .danhSachSanPham(danhSachSanPham)
                .lichSuThanhToan(lichSuThanhToan)
                .timelineTrangThai(timelineTrangThai);

        if (hoaDon.getPhieuGiamGia() != null) {
            builder.idPhieuGiamGia(hoaDon.getPhieuGiamGia().getId());
            builder.maPhieuGiamGia(hoaDon.getPhieuGiamGia().getMaPhieuGiamGia());
            builder.tenPhieuGiamGia(hoaDon.getPhieuGiamGia().getTenPhieuGiamGia());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public void capNhatTrangThaiHoaDon(Long id, Integer trangThaiMoi, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + id));

        NhanVien nguoiThucHien = getLoggedInEmployee();
        if (nguoiThucHien == null && (hoaDon.getLoaiHoaDon() == null || hoaDon.getLoaiHoaDon() != 2)) {
            nguoiThucHien = nhanVienRepository.findById(1L).orElse(null);
        }

        Integer trangThaiCu = hoaDon.getTrangThai();
        validateTrangThai(hoaDon.getLoaiHoaDon(), trangThaiCu, trangThaiMoi);

        // --- XỬ LÝ QUẢN LÝ TỒN KHO ---
        // 1. Bán hàng Online: Trừ tồn kho khi đơn hàng chuyển từ Chưa xác nhận (0) -> Đã xác nhận (1)
        if (hoaDon.getLoaiHoaDon() != null && hoaDon.getLoaiHoaDon() == 2 && trangThaiCu == 0 && trangThaiMoi == 1) {
            if (hoaDon.getDanhSachChiTiet() != null) {
                for (HoaDonChiTiet chiTiet : hoaDon.getDanhSachChiTiet()) {
                    ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                    if (ctsp != null) {
                        int soLuongBan = chiTiet.getSoLuong() != null ? chiTiet.getSoLuong() : 0;
                        int soLuongTon = ctsp.getSoLuongTon() != null ? ctsp.getSoLuongTon() : 0;
                        if (soLuongTon < soLuongBan) {
                            String tenSp = ctsp.getSanPham() != null ? ctsp.getSanPham().getTenSanPham() : "Sản phẩm";
                            String tenMs = ctsp.getMauSac() != null ? ctsp.getMauSac().getTenMauSac() : "";
                            String tenKt = ctsp.getKichThuoc() != null ? ctsp.getKichThuoc().getTenKichThuoc() : "";
                            throw new BadRequestException(String.format("Sản phẩm %s (%s, %s) không đủ số lượng trong kho. Hiện tại còn %d sản phẩm.",
                                    tenSp, tenMs, tenKt, soLuongTon));
                        }
                        ctsp.setSoLuongTon(soLuongTon - soLuongBan);
                        chiTietSanPhamRepository.save(ctsp);
                    }
                }
            }
        }

        // 2. Hoàn trả tồn kho khi Hủy đơn hàng (trangThaiMoi == 5)
        if (trangThaiMoi == 5) {
            if (hoaDon.getLoaiHoaDon() != null) {
                if (hoaDon.getLoaiHoaDon() == 0 || hoaDon.getLoaiHoaDon() == 1) {
                    // Offline/Tại quầy/Giao hàng: luôn trả lại kho vì tồn kho đã bị trừ lúc thêm vào giỏ hàng
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
                    // Online: chỉ trả lại kho nếu đơn hàng đã từng được xác nhận (trangThaiCu >= 1 && trangThaiCu <= 4)
                    if (trangThaiCu >= 1 && trangThaiCu <= 4) {
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
        }
        // -----------------------------

        hoaDon.setTrangThai(trangThaiMoi);
        hoaDon.setNguoiSua(nguoiThucHien != null ? nguoiThucHien.getHoVaTen() : "Hệ thống");

        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .hoaDon(hoaDon)
                .nhanVien(nguoiThucHien)
                .trangThai(trangThaiMoi)
                .ghiChu(ghiChu)
                .hanhDong(String.format("Cập nhật trạng thái đơn hàng - Phần thay đổi: Trạng thái đơn hàng - Từ: %s - Thành: %s",
                        getTrangThaiText(trangThaiCu), getTrangThaiText(trangThaiMoi)))
                .build();

        hoaDonRepository.save(hoaDon);
        lichSuHoaDonRepository.save(lichSu);
    }

    @Override
    public List<LichSuHoaDonResponseDTO> layLichSuHoaDon(Long id) {
        return lichSuHoaDonRepository.findByIdHoaDon(id);
    }

    private void validateTrangThai(Integer loaiDon, Integer trangThaiCu, Integer trangThaiMoi) {
        if (loaiDon == 0) { // Tại quầy
            if (!(trangThaiCu == 0 && (trangThaiMoi == 4 || trangThaiMoi == 5))) {
                throw new BadRequestException("Đơn tại quầy chỉ có thể chuyển từ 'Chưa xác nhận' sang 'Đã hoàn thành' hoặc 'Đã hủy'.");
            }
        } else { // Online
            Map<Integer, Integer> nextStateMap = Map.of(
                0, 1, // Chưa xác nhận -> Đã xác nhận
                1, 2, // Đã xác nhận -> Chờ giao
                2, 3, // Chờ giao -> Đang giao
                3, 4  // Đang giao -> Đã hoàn thành
            );
            
            Integer expectedNextState = nextStateMap.get(trangThaiCu);
            if (trangThaiMoi != 5 && (expectedNextState == null || !expectedNextState.equals(trangThaiMoi))) { // Cho phép hủy từ mọi trạng thái
                 throw new BadRequestException(String.format("Không thể chuyển trạng thái từ '%s' sang '%s'.", getTrangThaiText(trangThaiCu), getTrangThaiText(trangThaiMoi)));
            }
        }
    }

    private String getLoaiDonText(Integer loaiDon) {
        if (loaiDon == null) return "";
        return loaiDon == 0 ? "Tại quầy" : "Online/Giao hàng";
    }

    private String getTrangThaiText(Integer trangThai) {
        if (trangThai == null) return "";
        switch (trangThai) {
            case 0: return "Chưa xác nhận";
            case 1: return "Đã xác nhận";
            case 2: return "Chờ giao";
            case 3: return "Đang giao";
            case 4: return "Đã hoàn thành";
            case 5: return "Đã hủy";
            default: return "Không xác định";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonDetailResponseDTO traCuuHoaDon(String maHoaDon, String email) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Mã hóa đơn và email không được để trống.");
        }
        HoaDon hoaDon = hoaDonRepository.findByMaHoaDonAndEmail(maHoaDon.trim(), email.trim())
                 .orElseThrow(() -> new BadRequestException("Không tìm thấy đơn hàng với thông tin đã cung cấp. Vui lòng kiểm tra lại mã đơn và email."));
        return layChiTietHoaDon(hoaDon.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonDetailResponseDTO> layDanhSachHoaDonTheoKhachHang(Long khachHangId) {
        List<HoaDon> hoaDons = hoaDonRepository.findByKhachHangIdOrderByNgayTaoDesc(khachHangId);
        return hoaDons.stream().map(h -> layChiTietHoaDon(h.getId())).collect(Collectors.toList());
    }
}
