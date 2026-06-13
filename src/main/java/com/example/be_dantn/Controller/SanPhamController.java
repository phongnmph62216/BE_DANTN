package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.SanPhamCreateRequest;
import com.example.be_dantn.Dto.Request.SanPhamUpdateRequest;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Dto.Response.SanPhamDetailResponse;
import com.example.be_dantn.Dto.Response.SanPhamResponse;
import com.example.be_dantn.Entity.SanPham;
import com.example.be_dantn.sevicer.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<SanPhamResponse>>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idThuongHieu,
            @RequestParam(required = false) Long idChatLieu,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPhamResponse> productPage = sanPhamService.getSanPhamByFilter(keyword, idThuongHieu, idChatLieu, trangThai, pageable);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", productPage));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<SanPhamCreateRequest>> createProduct(@Valid @RequestBody SanPhamCreateRequest request) {
        try {
            SanPhamCreateRequest createdProduct = sanPhamService.createSanPham(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<>(HttpStatus.CREATED, "Thêm sản phẩm thành công", createdProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject<String>> toggleStatus(@PathVariable Long id) {
        try {
            SanPham updatedSanPham = sanPhamService.toggleStatus(id);
            String statusName = updatedSanPham.getTrangThai() == 1 ? "Hoạt động" : "Ngừng hoạt động";
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái thành công. Trạng thái hiện tại: " + statusName, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<SanPhamDetailResponse>> getProductById(@PathVariable Long id) {
        try {
            SanPhamDetailResponse productDTO = sanPhamService.findById(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết sản phẩm thành công", productDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<SanPhamDetailResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody SanPhamUpdateRequest request
    ) {
        try {
            SanPhamDetailResponse updatedProduct = sanPhamService.updateProduct(id, request);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật sản phẩm thành công", updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idThuongHieu,
            @RequestParam(required = false) Long idChatLieu,
            @RequestParam(required = false) Integer trangThai
    ) {
        try {
            byte[] excelData = sanPhamService.exportToExcel(keyword, idThuongHieu, idChatLieu, trangThai);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_san_pham.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/qr-scan/{maSanPham}")
    public ResponseEntity<ResponseObject<SanPhamDetailResponse>> getProductByQrCode(@PathVariable String maSanPham) {
        try {
            SanPhamDetailResponse productDTO = sanPhamService.findByQrCode(maSanPham);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Quét mã QR sản phẩm thành công", productDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }
    }
}
