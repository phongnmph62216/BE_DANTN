package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.CapNhatTrangThaiHoaDonRequest;
import com.example.be_dantn.Dto.HoaDonRequest;
import com.example.be_dantn.Dto.HoaDonResponse;
import com.example.be_dantn.entity.HoaDon;
import com.example.be_dantn.exception.CustomResourceotFoundException;
import com.example.be_dantn.repositoty.HoaDonRepository;
import com.example.be_dantn.sevicer.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final ModelMapper modelMapper;

    private HoaDonResponse toResponse(HoaDon hoaDon) {
        return modelMapper.map(hoaDon, HoaDonResponse.class);
    }

    private BigDecimal getOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String generateMaHoaDon() {
        return "HD" + System.currentTimeMillis();
    }

    @Override
    public List<HoaDonResponse> findAll() {
        return hoaDonRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public HoaDonResponse findById(Long id) {
        return hoaDonRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi id " + id + " khong ton tai"));
    }

    @Override
    public HoaDonResponse findByMaHoaDon(String maHoaDon) {
        return hoaDonRepository.findByMaHoaDon(maHoaDon)
                .map(this::toResponse)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi ma " + maHoaDon + " khong ton tai"));
    }

    @Override
    public HoaDonResponse add(HoaDonRequest request) {
        HoaDon hoaDon = modelMapper.map(request, HoaDon.class);

        if (hoaDon.getMaHoaDon() == null || hoaDon.getMaHoaDon().isBlank()) {
            hoaDon.setMaHoaDon(generateMaHoaDon());
        }

        if (hoaDonRepository.findByMaHoaDon(hoaDon.getMaHoaDon()).isPresent()) {
            throw new IllegalArgumentException("Ma hoa don da ton tai");
        }

        if (hoaDon.getLoaiHoaDon() == null || hoaDon.getLoaiHoaDon().isBlank()) {
            hoaDon.setLoaiHoaDon("TAI_QUAY");
        }

        if (hoaDon.getTrangThai() == null || hoaDon.getTrangThai().isBlank()) {
            hoaDon.setTrangThai("CHO_XAC_NHAN");
        }

        hoaDon.setPhiVanChuyen(getOrZero(hoaDon.getPhiVanChuyen()));
        hoaDon.setSoTienGoc(getOrZero(hoaDon.getSoTienGoc()));
        hoaDon.setSoTienGiam(getOrZero(hoaDon.getSoTienGiam()));

        BigDecimal tongTienThanhToan = hoaDon.getSoTienGoc()
                .subtract(hoaDon.getSoTienGiam())
                .add(hoaDon.getPhiVanChuyen());

        if (tongTienThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            tongTienThanhToan = BigDecimal.ZERO;
        }

        if (hoaDon.getMaHoaDon() == null || hoaDon.getMaHoaDon().isBlank()) {
            hoaDon.setMaHoaDon(generateMaHoaDon());
        }

        hoaDon.setMaQr(hoaDon.getMaHoaDon());

        hoaDon.setTongTienThanhToan(tongTienThanhToan);
        hoaDon.setNgayTao(LocalDateTime.now());

        return toResponse(hoaDonRepository.save(hoaDon));
    }

    @Override
    public HoaDonResponse update(HoaDonRequest request, Long id) {
        return hoaDonRepository.findById(id)
                .map(hoaDon -> {
                    if ("HOAN_THANH".equals(hoaDon.getTrangThai())) {
                        throw new IllegalArgumentException("Hoa don da hoan thanh khong duoc cap nhat");
                    }

                    if (request.getIdNhanVien() != null) {
                        hoaDon.setIdNhanVien(request.getIdNhanVien());
                    }

                    if (request.getIdKhachHang() != null) {
                        hoaDon.setIdKhachHang(request.getIdKhachHang());
                    }

                    if (request.getIdPhieuGiamGia() != null) {
                        hoaDon.setIdPhieuGiamGia(request.getIdPhieuGiamGia());
                    }

                    if (request.getMaHoaDon() != null && !request.getMaHoaDon().isBlank()) {
                        hoaDonRepository.findByMaHoaDon(request.getMaHoaDon())
                                .ifPresent(hd -> {
                                    if (!hd.getId().equals(id)) {
                                        throw new IllegalArgumentException("Ma hoa don da ton tai");
                                    }
                                });

                        hoaDon.setMaHoaDon(request.getMaHoaDon());
                    }

                    if (request.getLoaiHoaDon() != null) {
                        hoaDon.setLoaiHoaDon(request.getLoaiHoaDon());
                    }

                    if (request.getPhiVanChuyen() != null) {
                        hoaDon.setPhiVanChuyen(request.getPhiVanChuyen());
                    }

                    if (request.getSoTienGiam() != null) {
                        hoaDon.setSoTienGiam(request.getSoTienGiam());
                    }

                    if (request.getTenKhachHang() != null) {
                        hoaDon.setTenKhachHang(request.getTenKhachHang());
                    }

                    if (request.getDiaChiKhachHang() != null) {
                        hoaDon.setDiaChiKhachHang(request.getDiaChiKhachHang());
                    }

                    if (request.getSoDienThoai() != null) {
                        hoaDon.setSoDienThoai(request.getSoDienThoai());
                    }

                    if (request.getTrangThai() != null) {
                        hoaDon.setTrangThai(request.getTrangThai());
                    }

                    if (request.getGhiChu() != null) {
                        hoaDon.setGhiChu(request.getGhiChu());
                    }

                    if (request.getNgayNhanHang() != null) {
                        hoaDon.setNgayNhanHang(request.getNgayNhanHang());
                    }

                    if (request.getNgayThanhToan() != null) {
                        hoaDon.setNgayThanhToan(request.getNgayThanhToan());
                    }

                    hoaDon.setPhiVanChuyen(getOrZero(hoaDon.getPhiVanChuyen()));
                    hoaDon.setSoTienGoc(getOrZero(hoaDon.getSoTienGoc()));
                    hoaDon.setSoTienGiam(getOrZero(hoaDon.getSoTienGiam()));

                    BigDecimal tongTienThanhToan = hoaDon.getSoTienGoc()
                            .subtract(hoaDon.getSoTienGiam())
                            .add(hoaDon.getPhiVanChuyen());

                    if (tongTienThanhToan.compareTo(BigDecimal.ZERO) < 0) {
                        tongTienThanhToan = BigDecimal.ZERO;
                    }

                    hoaDon.setTongTienThanhToan(tongTienThanhToan);
                    hoaDon.setNgaySua(LocalDateTime.now());

                    return toResponse(hoaDonRepository.save(hoaDon));
                })
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi id " + id + " khong ton tai"));
    }

    @Override
    public HoaDonResponse capNhatTrangThai(Long id, CapNhatTrangThaiHoaDonRequest request) {
        return hoaDonRepository.findById(id)
                .map(hoaDon -> {
                    hoaDon.setTrangThai(request.getTrangThai());

                    if (request.getGhiChu() != null) {
                        hoaDon.setGhiChu(request.getGhiChu());
                    }

                    if ("HOAN_THANH".equals(request.getTrangThai())) {
                        hoaDon.setNgayThanhToan(LocalDateTime.now());
                    }

                    if ("DA_NHAN_HANG".equals(request.getTrangThai())) {
                        hoaDon.setNgayNhanHang(LocalDateTime.now());
                    }

                    hoaDon.setNgaySua(LocalDateTime.now());

                    return toResponse(hoaDonRepository.save(hoaDon));
                })
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi id " + id + " khong ton tai"));
    }

    @Override
    public void delete(Long id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi id " + id + " khong ton tai"));

        if ("HOAN_THANH".equals(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Hoa don da hoan thanh khong duoc xoa");
        }

        hoaDonRepository.deleteById(id);
    }
    @Override
    public byte[] exportExcel() throws IOException {
        List<HoaDon> hoaDons = hoaDonRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("HoaDon");

        String[] columns = {
                "STT", "Mã hóa đơn", "Khách hàng", "Số điện thoại",
                "Loại hóa đơn", "Số tiền gốc", "Giảm giá",
                "Phí vận chuyển", "Tổng thanh toán", "Trạng thái", "Ngày tạo"
        };

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DANH SÁCH HÓA ĐƠN");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.length - 1));

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(headerStyle);

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(textStyle);

        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(centerStyle);

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(moneyStyle);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorder(dateStyle);

        Row header = sheet.createRow(2);
        header.setHeightInPoints(24);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 3;
        int stt = 1;

        for (HoaDon hd : hoaDons) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(22);

            createCell(row, 0, stt++, centerStyle);
            createCell(row, 1, hd.getMaHoaDon(), textStyle);
            createCell(row, 2, hd.getTenKhachHang(), textStyle);
            createCell(row, 3, hd.getSoDienThoai(), textStyle);
            createCell(row, 4, hd.getLoaiHoaDon(), centerStyle);
            createCell(row, 5, toDouble(hd.getSoTienGoc()), moneyStyle);
            createCell(row, 6, toDouble(hd.getSoTienGiam()), moneyStyle);
            createCell(row, 7, toDouble(hd.getPhiVanChuyen()), moneyStyle);
            createCell(row, 8, toDouble(hd.getTongTienThanhToan()), moneyStyle);
            createCell(row, 9, hd.getTrangThai(), centerStyle);

            Cell dateCell = row.createCell(10);
            if (hd.getNgayTao() != null) {
                dateCell.setCellValue(java.sql.Timestamp.valueOf(hd.getNgayTao()));
            } else {
                dateCell.setCellValue("");
            }
            dateCell.setCellStyle(dateStyle);
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 800);
        }

        sheet.createFreezePane(0, 3);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int column, Integer value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? 0 : value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private double toDouble(BigDecimal value) {
        return value == null ? 0 : value.doubleValue();
    }
    @Override
    public HoaDonResponse huyHoaDon(Long id, CapNhatTrangThaiHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new CustomResourceotFoundException("Hoa don voi id " + id + " khong ton tai"));

        if ("HOAN_THANH".equals(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Hoa don da hoan thanh khong duoc huy");
        }

        if ("DA_HUY".equals(hoaDon.getTrangThai())) {
            throw new IllegalArgumentException("Hoa don da bi huy truoc do");
        }

        hoaDon.setTrangThai("DA_HUY");

        if (request != null && request.getGhiChu() != null) {
            hoaDon.setGhiChu(request.getGhiChu());
        }

        hoaDon.setNgaySua(LocalDateTime.now());

        return toResponse(hoaDonRepository.save(hoaDon));
    }
}