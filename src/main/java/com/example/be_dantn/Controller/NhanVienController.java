package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.NhanVienCreateRequest;
import com.example.be_dantn.Dto.Request.NhanVienUpdateRequest;
import com.example.be_dantn.Dto.Response.NhanVienResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Entity.NhanVien;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.sevicer.NhanVienService;
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

@RestController
@RequestMapping("/api/v1/nhan-vien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<NhanVienResponseDTO>>> getNhanViens(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVienResponseDTO> nhanVienPage = nhanVienService.getByFilters(keyword, trangThai, pageable);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách nhân viên thành công", nhanVienPage));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject<Void>> toggleStatus(@PathVariable Long id) {
        try {
            nhanVienService.toggleStatus(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái thành công", null));
        } catch (ResourceNotFoundException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai
    ) {
        try {
            byte[] excelFile = nhanVienService.exportNhanVienToExcel(keyword, trangThai);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_nhan_vien.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelFile);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<NhanVien>> getNhanVienById(@PathVariable Long id) {
        try {
            NhanVien nhanVien = nhanVienService.getNhanVienById(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết nhân viên thành công", nhanVien));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<NhanVien>> updateNhanVien(
            @PathVariable Long id,
            @Valid @RequestBody NhanVienUpdateRequest request
    ) {
        try {
            NhanVien updatedNhanVien = nhanVienService.updateNhanVien(id, request);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật nhân viên thành công", updatedNhanVien));
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

    @PostMapping
    public ResponseEntity<ResponseObject<NhanVien>> createNhanVien(
            @Valid @RequestBody NhanVienCreateRequest request
    ) {
        try {
            NhanVien newNhanVien = nhanVienService.createNhanVien(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject<>(HttpStatus.CREATED, "Thêm mới nhân viên thành công", newNhanVien));
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
