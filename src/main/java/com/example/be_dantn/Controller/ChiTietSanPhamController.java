package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.ChiTietSanPhamUpdateRequest;
import com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.ChiTietSanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/chi-tiet-san-pham")
@RequiredArgsConstructor
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<ChiTietSanPhamResponseDTO>>> getVariants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idMauSac,
            @RequestParam(required = false) Long idKichThuoc,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChiTietSanPhamResponseDTO> variantPage = chiTietSanPhamService.getVariantsByFilter(keyword, idMauSac, idKichThuoc, trangThai, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách biến thể thành công", variantPage));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject<Void>> toggleStatus(@PathVariable Long id) {
        try {
            chiTietSanPhamService.toggleStatus(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idMauSac,
            @RequestParam(required = false) Long idKichThuoc,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        try {
            byte[] excelData = chiTietSanPhamService.exportToExcel(keyword, idMauSac, idKichThuoc, trangThai, minPrice, maxPrice);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_bien_the.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<ChiTietSanPhamResponseDTO>> getVariantById(@PathVariable Long id) {
        try {
            ChiTietSanPhamResponseDTO variantDTO = chiTietSanPhamService.findById(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết biến thể thành công", variantDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<ChiTietSanPhamResponseDTO>> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody ChiTietSanPhamUpdateRequest request
    ) {
        try {
            ChiTietSanPhamResponseDTO updatedVariant = chiTietSanPhamService.updateVariant(id, request);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật biến thể thành công", updatedVariant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @GetMapping("/qr-scan/{maChiTietSanPham}")
    public ResponseEntity<ResponseObject<ChiTietSanPhamResponseDTO>> getVariantByQrCode(@PathVariable String maChiTietSanPham) {
        try {
            ChiTietSanPhamResponseDTO variantDTO = chiTietSanPhamService.findByQrCode(maChiTietSanPham);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Quét mã QR thành công", variantDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }
    }
}
