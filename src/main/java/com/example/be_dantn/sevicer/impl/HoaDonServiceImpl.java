package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.HoaDonChiTietDTO;
import com.example.be_dantn.Dto.Response.HoaDonDetailResponseDTO;
import com.example.be_dantn.Dto.Response.LichSuHoaDonDTO;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.ThanhToanDTO;
import com.example.be_dantn.Entity.DiaChi;
import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.Entity.KhachHang;
import com.example.be_dantn.Entity.LichSuHoaDon;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.exception.BadRequestException;
import com.example.be_dantn.Repository.*;
import com.example.be_dantn.Repository.NhanVienRepository;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private NhanVienRepository nhanVienRepository; // Giả sử bạn có NhanVienRepository

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
        List<LichSuHoaDonResponseDTO> timelineTrangThai = lichSuHoaDonRepository.findByIdHoaDon(id);
        
        List<LichSuHoaDonDTO> simplifiedTimeline = timelineTrangThai.stream()
            .map(l -> new LichSuHoaDonDTO(l.getTrangThai(), l.getThoiGian(), l.getGhiChu()))
            .collect(Collectors.toList());

        BigDecimal tongTienHang = danhSachSanPham.stream()
                .map(HoaDonChiTietDTO::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String diaChiGiaoHang = Optional.ofNullable(hoaDon.getKhachHang())
                .map(KhachHang::getDanhSachDiaChi)
                .flatMap(list -> list.stream().filter(DiaChi::getKieuDiaChiLaMacDinh).findFirst())
                .map(DiaChi::toString) // Hoặc một phương thức format địa chỉ đẹp hơn
                .orElse(null);

        return HoaDonDetailResponseDTO.builder()
                .maHoaDon(hoaDon.getMaHoaDon())
                .ngayTao(hoaDon.getNgayTao())
                .nhanVienTao(hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getHoVaTen() : "N/A")
                .nhanVienCapNhat(null) // Cần logic để lấy nhân viên cập nhật gần nhất
                .trangThai(hoaDon.getTrangThai())
                .tenKhachHang(hoaDon.getTenKhachHang())
                .sdtKhachHang(hoaDon.getSoDienThoai())
                .emailKhachHang(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getEmail() : null)
                .diaChiGiaoHang(diaChiGiaoHang)
                .loaiDon(hoaDon.getLoaiHoaDon())
                .ghiChu(null) // Cần logic để lấy ghi chú
                .tongTienHang(tongTienHang)
                .giamGia(BigDecimal.ZERO) // Cần logic tính giảm giá
                .phiVanChuyen(BigDecimal.ZERO) // Cần logic phí vận chuyển
                .tongTienThanhToan(hoaDon.getTongTienThanhToan())
                .danhSachSanPham(danhSachSanPham)
                .lichSuThanhToan(lichSuThanhToan)
                .timelineTrangThai(simplifiedTimeline)
                .build();
    }

    @Override
    @Transactional
    public void capNhatTrangThaiHoaDon(Long id, Integer trangThaiMoi, String ghiChu) {
        // Tạm thời lấy nhân viên đầu tiên trong DB để thực hiện hành động
        // TRONG THỰC TẾ: Sẽ lấy từ SecurityContextHolder
        NhanVien nguoiThucHien = nhanVienRepository.findById(1L).orElse(null);

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy hóa đơn với ID: " + id));

        Integer trangThaiCu = hoaDon.getTrangThai();
        validateTrangThai(hoaDon.getLoaiHoaDon(), trangThaiCu, trangThaiMoi);

        hoaDon.setTrangThai(trangThaiMoi);

        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .hoaDon(hoaDon)
                .nhanVien(nguoiThucHien) // Gán nhân viên thực hiện
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
            if (!(trangThaiCu == 0 && trangThaiMoi == 4)) {
                throw new BadRequestException("Đơn tại quầy chỉ có thể chuyển từ 'Chưa xác nhận' sang 'Đã hoàn thành'.");
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
}
