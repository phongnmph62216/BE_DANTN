package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.PhieuGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Request.PhieuGiamGiaUpdateRequest;
import com.example.be_dantn.Dto.Response.PhieuGiamGiaResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.sevicer.PhieuGiamGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/phieu-giam-gia")
@RequiredArgsConstructor
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService phieuGiamGiaService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<PhieuGiamGiaResponseDTO>>> getPhieuGiamGia(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer loaiGiam,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PhieuGiamGiaResponseDTO> phieuPage = phieuGiamGiaService.getByFilters(keyword, loaiGiam, tuNgay, denNgay, trangThai, pageable);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách phiếu giảm giá thành công", phieuPage));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject<Void>> toggleStatus(@PathVariable Long id) {
        try {
            phieuGiamGiaService.toggleStatus(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseObject<Void>> createPhieuGiamGia(
            @Valid @RequestBody PhieuGiamGiaCreateRequest request
    ) {
        try {
            phieuGiamGiaService.createPhieuGiamGia(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject<>(HttpStatus.CREATED, "Tạo phiếu giảm giá thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<PhieuGiamGiaResponseDTO>> getPhieuGiamGiaById(@PathVariable Long id) {
        try {
            PhieuGiamGiaResponseDTO response = phieuGiamGiaService.getPhieuGiamGiaById(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy thông tin phiếu giảm giá thành công", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> updatePhieuGiamGia(
            @PathVariable Long id,
            @Valid @RequestBody PhieuGiamGiaUpdateRequest request
    ) {
        try {
            phieuGiamGiaService.updatePhieuGiamGia(id, request);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật phiếu giảm giá thành công", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }
}
