package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.ChiTietSanPhamUpdateRequest;
import com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO;
import com.example.be_dantn.Entity.ChiTietSanPham;
import com.example.be_dantn.Repository.ChiTietSanPhamRepository;
import com.example.be_dantn.sevicer.ChiTietSanPhamService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChiTietSanPhamServiceImpl implements ChiTietSanPhamService {

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Override
    public Page<ChiTietSanPhamResponseDTO> getVariantsByFilter(String keyword, Long idMauSac, Long idKichThuoc, Integer trangThai, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return chiTietSanPhamRepository.findByFilters(keyword, idMauSac, idKichThuoc, trangThai, minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional
    public void toggleStatus(Long id) {
        ChiTietSanPham variant = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với id: " + id));
        variant.setTrangThai(variant.getTrangThai() == 1 ? 0 : 1);
        chiTietSanPhamRepository.save(variant);
    }

    @Override
    public byte[] exportToExcel(String keyword, Long idMauSac, Long idKichThuoc, Integer trangThai, BigDecimal minPrice, BigDecimal maxPrice) throws IOException {
        List<ChiTietSanPhamResponseDTO> variants = chiTietSanPhamRepository.findByFiltersForExcel(keyword, idMauSac, idKichThuoc, trangThai, minPrice, maxPrice);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("DanhSachBienThe");

            // Header
            String[] headers = {"STT", "Link Ảnh", "Mã SP", "Mã CTSP", "Kích Cỡ", "Màu Sắc", "Tồn Kho", "Giá Nhập", "Giá Bán", "Trạng Thái"};
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Data
            int rowNum = 1;
            for (ChiTietSanPhamResponseDTO variant : variants) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(variant.getAnh() != null ? variant.getAnh() : "");
                row.createCell(2).setCellValue(variant.getMaSanPham());
                row.createCell(3).setCellValue(variant.getMaChiTietSanPham());
                row.createCell(4).setCellValue(variant.getTenKichCo());
                row.createCell(5).setCellValue(variant.getTenMauSac());
                row.createCell(6).setCellValue(variant.getSoLuongTon());
                row.createCell(7).setCellValue(variant.getGiaNhap() != null ? variant.getGiaNhap().toString() : "0");
                row.createCell(8).setCellValue(variant.getGiaBan() != null ? variant.getGiaBan().toString() : "0");
                row.createCell(9).setCellValue(variant.getTrangThai() == 1 ? "Đang bán" : "Ngừng bán");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public ChiTietSanPhamResponseDTO findById(Long id) {
        ChiTietSanPham variant = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với id: " + id));
        return toDTO(variant);
    }

    @Override
    @Transactional
    public ChiTietSanPhamResponseDTO updateVariant(Long id, ChiTietSanPhamUpdateRequest request) {
        ChiTietSanPham variant = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm với id: " + id));

        // Cập nhật các trường từ request
        variant.setGiaNhap(request.getGiaNhap());
        variant.setGiaBan(request.getGiaBan());
        variant.setSoLuongTon(request.getSoLuongTon());
        variant.setAnh(request.getAnh());
        variant.setTrangThai(request.getTrangThai());

        ChiTietSanPham updatedVariant = chiTietSanPhamRepository.save(variant);
        return toDTO(updatedVariant);
    }

    @Override
    public ChiTietSanPhamResponseDTO findByQrCode(String maChiTietSanPham) {
        ChiTietSanPham variant = chiTietSanPhamRepository.findByMaChiTietSanPham(maChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể nào với mã QR này: " + maChiTietSanPham));
        return toDTO(variant);
    }

    /**
     * Helper method để chuyển đổi Entity sang DTO.
     * Cần thiết vì chúng ta cần join để lấy các tên thuộc tính.
     */
    private ChiTietSanPhamResponseDTO toDTO(ChiTietSanPham entity) {
        return new ChiTietSanPhamResponseDTO(
                entity.getId(),
                entity.getAnh(),
                entity.getSanPham() != null ? entity.getSanPham().getMaSanPham() : null,
                entity.getMaChiTietSanPham(),
                entity.getKichThuoc() != null ? entity.getKichThuoc().getTenKichThuoc() : null,
                entity.getMauSac() != null ? entity.getMauSac().getTenMauSac() : null,
                entity.getSoLuongTon(),
                entity.getGiaNhap(),
                entity.getGiaBan(),
                entity.getTrangThai()
        );
    }
}
