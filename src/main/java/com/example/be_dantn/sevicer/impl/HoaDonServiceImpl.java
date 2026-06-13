package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.HoaDonUpdateTrangThaiRequest;
import com.example.be_dantn.Dto.Response.HoaDonResponseDTO;
import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.Entity.LichSuHoaDon;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.Repository.HoaDonRepository;
import com.example.be_dantn.Repository.LichSuHoaDonRepository;
import com.example.be_dantn.Repository.NhanVienRepository;
import com.example.be_dantn.sevicer.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    public Page<HoaDonResponseDTO> getByFilters(
            String keyword,
            Integer loaiHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer trangThai,
            Pageable pageable
    ) {
        String keywordFilter = keyword == null || keyword.trim().isEmpty()
                ? null
                : keyword.trim();

        return hoaDonRepository.findByFilters(
                keywordFilter,
                loaiHoaDon,
                tuNgay,
                denNgay,
                trangThai,
                pageable
        );
    }

    @Override
    public HoaDonResponseDTO getHoaDonById(Long id) {
        return hoaDonRepository.findResponseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + id));
    }

    @Override
    @Transactional
    public void updateTrangThai(Long id, HoaDonUpdateTrangThaiRequest request) {
        Integer nextStatus = request.getTrangThai();
        validateStatus(nextStatus);

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với id: " + id));

        Integer oldStatus = hoaDon.getTrangThai();

        if (oldStatus != null && oldStatus == 4 && nextStatus != 4) {
            throw new IllegalArgumentException("Hóa đơn đã hoàn thành, không thể chuyển sang trạng thái khác.");
        }

        if (oldStatus != null && oldStatus == 5 && nextStatus != 5) {
            throw new IllegalArgumentException("Hóa đơn đã hủy, không thể chuyển sang trạng thái khác.");
        }

        hoaDon.setTrangThai(nextStatus);

        if (nextStatus == 4) {
            LocalDateTime now = LocalDateTime.now();

            if (hoaDon.getNgayThanhToan() == null) {
                hoaDon.setNgayThanhToan(now);
            }

            if (hoaDon.getNgayNhanHang() == null) {
                hoaDon.setNgayNhanHang(now);
            }
        }

        hoaDonRepository.save(hoaDon);

        NhanVien nhanVien = null;
        String nguoiThucHien = "Hệ thống";

        if (request.getIdNhanVien() != null) {
            nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với id: " + request.getIdNhanVien()));
            nguoiThucHien = nhanVien.getHoVaTen();
        } else if (hoaDon.getNhanVien() != null) {
            nhanVien = hoaDon.getNhanVien();
            nguoiThucHien = hoaDon.getNhanVien().getHoVaTen();
        }

        String hanhDong = request.getHanhDong();
        if (hanhDong == null || hanhDong.trim().isEmpty()) {
            hanhDong = getStatusText(nextStatus);
        }

        String ghiChu = request.getGhiChu();
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            ghiChu = "Cập nhật trạng thái sang " + getStatusText(nextStatus);
        }

        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setNhanVien(nhanVien);
        lichSu.setTrangThai(nextStatus);
        lichSu.setHanhDong(hanhDong);
        lichSu.setGhiChu(ghiChu);
        lichSu.setNguoiThucHien(nguoiThucHien);
        lichSu.setThoiGian(LocalDateTime.now());

        lichSuHoaDonRepository.save(lichSu);
    }

    private void validateStatus(Integer status) {
        if (status == null || status < 0 || status > 5) {
            throw new IllegalArgumentException("Trạng thái hóa đơn không hợp lệ.");
        }
    }

    private String getStatusText(Integer status) {
        if (status == null) {
            return "Không xác định";
        }

        return switch (status) {
            case 0 -> "Tạo mới";
            case 1 -> "Chờ xác nhận";
            case 2 -> "Đã xác nhận";
            case 3 -> "Đang giao";
            case 4 -> "Hoàn thành";
            case 5 -> "Đã hủy";
            default -> "Không xác định";
        };
    }
    @Override
    public byte[] exportExcel(
            String keyword,
            Integer loaiHoaDon,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            Integer trangThai
    ) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            String keywordFilter = keyword == null || keyword.trim().isEmpty()
                    ? null
                    : keyword.trim();

            List<HoaDonResponseDTO> hoaDons = hoaDonRepository.findByFilters(
                    keywordFilter,
                    loaiHoaDon,
                    tuNgay,
                    denNgay,
                    trangThai,
                    Pageable.unpaged()
            ).getContent();

            Sheet sheet = workbook.createSheet("Danh sách hóa đơn");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle moneyStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));

            String[] headers = {
                    "STT",
                    "Mã hóa đơn",
                    "Loại hóa đơn",
                    "Khách hàng",
                    "Số điện thoại",
                    "Email",
                    "Nhân viên",
                    "Mã nhân viên",
                    "Phiếu giảm giá",
                    "Tiền gốc",
                    "Tiền giảm",
                    "Phí vận chuyển",
                    "Tổng thanh toán",
                    "Trạng thái",
                    "Ngày tạo",
                    "Ngày thanh toán",
                    "Ngày nhận hàng",
                    "Ghi chú",
                    "Địa chỉ"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;

            for (int i = 0; i < hoaDons.size(); i++) {
                HoaDonResponseDTO item = hoaDons.get(i);
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(safeString(item.getMaHoaDon()));
                row.createCell(2).setCellValue(getLoaiHoaDonText(item.getLoaiHoaDon()));
                row.createCell(3).setCellValue(safeString(item.getTenKhachHang()));
                row.createCell(4).setCellValue(safeString(item.getSoDienThoai()));
                row.createCell(5).setCellValue(safeString(item.getEmailKhachHang()));
                row.createCell(6).setCellValue(safeString(item.getTenNhanVien()));
                row.createCell(7).setCellValue(safeString(item.getMaNhanVien()));
                row.createCell(8).setCellValue(safeString(item.getMaPhieuGiamGia()));

                createMoneyCell(row, 9, item.getSoTienGoc(), moneyStyle);
                createMoneyCell(row, 10, item.getSoTienGiam(), moneyStyle);
                createMoneyCell(row, 11, item.getPhiVanChuyen(), moneyStyle);
                createMoneyCell(row, 12, item.getTongTienThanhToan(), moneyStyle);

                row.createCell(13).setCellValue(getStatusText(item.getTrangThai()));
                row.createCell(14).setCellValue(item.getNgayTao() != null ? item.getNgayTao().toString() : "-");
                row.createCell(15).setCellValue(item.getNgayThanhToan() != null ? item.getNgayThanhToan().toString() : "-");
                row.createCell(16).setCellValue(item.getNgayNhanHang() != null ? item.getNgayNhanHang().toString() : "-");
                row.createCell(17).setCellValue(safeString(item.getGhiChu()));
                row.createCell(18).setCellValue(safeString(item.getDiaChi()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Không thể xuất Excel hóa đơn: " + e.getMessage(), e);
        }
    }

    private void createMoneyCell(Row row, int index, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value != null ? value.doubleValue() : 0);
        cell.setCellStyle(style);
    }

    private String safeString(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String getLoaiHoaDonText(Integer type) {
        if (type == null) {
            return "Không xác định";
        }

        return switch (type) {
            case 0 -> "Online";
            case 1 -> "Tại quầy";
            case 2 -> "Giao hàng";
            default -> "Không xác định";
        };
    }
}