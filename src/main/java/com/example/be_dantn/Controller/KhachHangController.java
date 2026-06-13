package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.DiaChiRequest;
import com.example.be_dantn.Dto.Request.KhachHangRequest;
import com.example.be_dantn.Dto.Response.KhachHangDetailResponseDTO;
import com.example.be_dantn.Dto.Response.KhachHangResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.KhachHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService khachHangService;

    // --- NHÓM 1: API KHÁCH HÀNG ---

    @GetMapping
    public ResponseEntity<ResponseObject<Page<KhachHangResponseDTO>>> getKhachHangs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer gioiTinh,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHangResponseDTO> khachHangs = khachHangService.getByFilters(keyword, gioiTinh, trangThai, pageable);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách khách hàng thành công", khachHangs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<KhachHangDetailResponseDTO>> getKhachHangDetail(@PathVariable Long id) {
        KhachHangDetailResponseDTO detail = khachHangService.getKhachHangDetail(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết khách hàng thành công", detail));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<Void>> createKhachHang(@Valid @RequestBody KhachHangRequest request) {
        khachHangService.createKhachHang(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject<>(HttpStatus.CREATED, "Thêm mới khách hàng thành công", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> updateKhachHang(
            @PathVariable Long id,
            @Valid @RequestBody KhachHangRequest request
    ) {
        khachHangService.updateKhachHang(id, request);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật thông tin khách hàng thành công", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject<Void>> toggleStatus(@PathVariable Long id) {
        khachHangService.toggleStatus(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái khách hàng thành công", null));
    }

    @GetMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportExcel() throws IOException {
        List<KhachHangResponseDTO> khachHangs = khachHangService.getAllForExcel();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("KhachHang");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Mã KH", "Họ Tên", "SĐT", "Email", "Giới Tính", "Địa Chỉ Mặc Định", "Trạng Thái"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            // Style header...
        }

        // Data
        int rowNum = 1;
        for (KhachHangResponseDTO kh : khachHangs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(kh.getId());
            row.createCell(1).setCellValue(kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "");
            row.createCell(2).setCellValue(kh.getHoTen() != null ? kh.getHoTen() : "");
            row.createCell(3).setCellValue(kh.getSdt() != null ? kh.getSdt() : "");
            row.createCell(4).setCellValue(kh.getEmail() != null ? kh.getEmail() : "");
            
            String gioiTinhStr = "";
            if (kh.getGioiTinh() != null) {
                if (kh.getGioiTinh() == 1) gioiTinhStr = "Nam";
                else if (kh.getGioiTinh() == 0) gioiTinhStr = "Nữ";
                else gioiTinhStr = "Khác";
            }
            row.createCell(5).setCellValue(gioiTinhStr);
            
            row.createCell(6).setCellValue(kh.getDiaChiMacDinh() != null ? kh.getDiaChiMacDinh() : "");
            row.createCell(7).setCellValue(kh.getTrangThai() != null && kh.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_khach_hang.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    // --- NHÓM 2: API ĐỊA CHỈ KHÁCH HÀNG ---

    @PostMapping("/{khachHangId}/dia-chi")
    public ResponseEntity<ResponseObject<Void>> addDiaChi(
            @PathVariable Long khachHangId,
            @Valid @RequestBody DiaChiRequest request
    ) {
        khachHangService.addDiaChi(khachHangId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject<>(HttpStatus.CREATED, "Thêm mới địa chỉ thành công", null));
    }

    @PatchMapping("/{khachHangId}/dia-chi/{diaChiId}/mac-dinh")
    public ResponseEntity<ResponseObject<Void>> setDiaChiMacDinh(
            @PathVariable Long khachHangId,
            @PathVariable Long diaChiId
    ) {
        try {
            khachHangService.setDiaChiMacDinh(khachHangId, diaChiId);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật địa chỉ mặc định thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }
}
