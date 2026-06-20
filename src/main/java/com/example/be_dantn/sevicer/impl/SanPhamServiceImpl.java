package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.SanPhamCreateRequest;
import com.example.be_dantn.Dto.Request.SanPhamUpdateRequest;
import com.example.be_dantn.Dto.Response.SanPhamDetailResponse;
import com.example.be_dantn.Dto.Response.SanPhamResponse;
import com.example.be_dantn.Entity.*;
import com.example.be_dantn.Repository.*;
import com.example.be_dantn.sevicer.SanPhamService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final EntityManager entityManager;
    private final com.example.be_dantn.Config.CodeGenerator codeGenerator;

    @Override
    public Page<SanPhamResponse> getSanPhamByFilter(String keyword, Long idThuongHieu, Long idChatLieu, Integer trangThai, Pageable pageable) {
        return sanPhamRepository.findSanPhamByFilters(keyword, idThuongHieu, idChatLieu, trangThai, pageable);
    }

    @Override
    @Transactional
    public SanPhamCreateRequest createSanPham(SanPhamCreateRequest request) {
        // Step 1: Handle maSanPham
        String maSanPham = request.getMaSanPham();
        if (!StringUtils.hasText(maSanPham)) {
            maSanPham = codeGenerator.generateCode("san_pham", "ma_san_pham", "SP");
        } else {
            if (sanPhamRepository.existsByMaSanPham(maSanPham)) {
                throw new RuntimeException("Mã sản phẩm " + maSanPham + " đã tồn tại.");
            }
        }

        // Step 2: Create and Save SanPham
        SanPham sanPham = SanPham.builder()
                .maSanPham(maSanPham)
                .tenSanPham(request.getTenSanPham())
                .moTa(request.getMoTa())
                .hinhAnh(request.getHinhAnh())
                .trangThai(1) // Default status is active
                .thuongHieu(entityManager.getReference(ThuongHieu.class, request.getIdThuongHieu()))
                .xuatSu(entityManager.getReference(XuatSu.class, request.getIdXuatSu()))
                .chatLieu(entityManager.getReference(ChatLieu.class, request.getIdChatLieu()))
                .loaiSanPham(entityManager.getReference(LoaiSanPham.class, request.getIdLoaiSanPham()))
                .kieuDang(entityManager.getReference(KieuDang.class, request.getIdKieuDang()))
                .coAo(entityManager.getReference(CoAo.class, request.getIdCoAo()))
                .tayAo(entityManager.getReference(TayAo.class, request.getIdTayAo()))
                .vaiAo(entityManager.getReference(VaiAo.class, request.getIdVaiAo()))
                .build();

        SanPham savedSanPham = sanPhamRepository.save(sanPham);

        // Step 3: Create ChiTietSanPham list
        List<ChiTietSanPham> bienTheList = new ArrayList<>();
        if (request.getDanhSachBienThe() != null && !request.getDanhSachBienThe().isEmpty()) {
            for (SanPhamCreateRequest.BienTheRequest bienTheRequest : request.getDanhSachBienThe()) {
                ChiTietSanPham chiTiet = ChiTietSanPham.builder()
                        .sanPham(savedSanPham)
                        .mauSac(entityManager.getReference(MauSac.class, bienTheRequest.getIdMauSac()))
                        .kichThuoc(entityManager.getReference(KichThuoc.class, bienTheRequest.getIdKichThuoc()))
                        .soLuongTon(bienTheRequest.getSoLuongTon())
                        .giaNhap(bienTheRequest.getGiaNhap())
                        .giaBan(bienTheRequest.getGiaBan())
                        .anh(bienTheRequest.getAnh())
                        .build();
                bienTheList.add(chiTiet);
            }
        }

        // Step 4: Bulk Save
        chiTietSanPhamRepository.saveAll(bienTheList);

        request.setMaSanPham(savedSanPham.getMaSanPham()); // Return the generated code if it was created
        return request;
    }

    @Override
    @Transactional
    public SanPham toggleStatus(Long id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
        
        // Đảo ngược trạng thái
        int newStatus = sanPham.getTrangThai() == 1 ? 0 : 1;
        sanPham.setTrangThai(newStatus);
        sanPham.setNgaySua(LocalDateTime.now());
        
        return sanPhamRepository.save(sanPham);
    }

    @Override
    public SanPhamDetailResponse findById(Long id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
        return SanPhamDetailResponse.fromEntity(sanPham);
    }

    @Override
    @Transactional
    public SanPhamDetailResponse updateProduct(Long id, SanPhamUpdateRequest request) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        // Cập nhật các trường cơ bản
        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setHinhAnh(request.getHinhAnh());
        sanPham.setTrangThai(request.getTrangThai());

        // Cập nhật 8 thuộc tính
        sanPham.setThuongHieu(entityManager.getReference(ThuongHieu.class, request.getIdThuongHieu()));
        sanPham.setChatLieu(entityManager.getReference(ChatLieu.class, request.getIdChatLieu()));
        sanPham.setXuatSu(entityManager.getReference(XuatSu.class, request.getIdXuatSu()));
        sanPham.setKieuDang(entityManager.getReference(KieuDang.class, request.getIdKieuDang()));
        sanPham.setLoaiSanPham(entityManager.getReference(LoaiSanPham.class, request.getIdLoaiSanPham()));
        sanPham.setCoAo(entityManager.getReference(CoAo.class, request.getIdCoAo()));
        sanPham.setTayAo(entityManager.getReference(TayAo.class, request.getIdTayAo()));
        sanPham.setVaiAo(entityManager.getReference(VaiAo.class, request.getIdVaiAo()));

        SanPham updatedSanPham = sanPhamRepository.save(sanPham);
        return SanPhamDetailResponse.fromEntity(updatedSanPham);
    }

    @Override
    public byte[] exportToExcel(String keyword, Long idThuongHieu, Long idChatLieu, Integer trangThai) throws IOException {
        List<SanPhamResponse> products = sanPhamRepository.findSanPhamByFiltersForExcel(keyword, idThuongHieu, idChatLieu, trangThai);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("DanhSachSanPham");

            // Header
            String[] headers = {"STT", "Mã SP", "Tên Sản Phẩm", "Thương Hiệu", "Chất Liệu", "Tồn Kho", "Khoảng Giá", "Trạng Thái"};
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
            for (SanPhamResponse product : products) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(product.getMaSanPham());
                row.createCell(2).setCellValue(product.getTenSanPham());
                row.createCell(3).setCellValue(product.getTenThuongHieu());
                row.createCell(4).setCellValue(product.getTenChatLieu());
                row.createCell(5).setCellValue(product.getTongTonKho() != null ? product.getTongTonKho().toString() : "0");

                String priceRange = (product.getGiaThapNhat() != null ? product.getGiaThapNhat().toString() : "N/A") +
                                    " - " +
                                    (product.getGiaCaoNhat() != null ? product.getGiaCaoNhat().toString() : "N/A");
                row.createCell(6).setCellValue(priceRange);
                row.createCell(7).setCellValue(product.getTrangThai() == 1 ? "Kinh doanh" : "Ngừng kinh doanh");
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
    public SanPhamDetailResponse findByQrCode(String maSanPham) {
        SanPham sanPham = sanPhamRepository.findByMaSanPham(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã QR: " + maSanPham));
        return SanPhamDetailResponse.fromEntity(sanPham);
    }
}
