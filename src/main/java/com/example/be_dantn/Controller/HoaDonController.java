package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.HoaDonUpdateTrangThaiRequest;
import com.example.be_dantn.Dto.Response.HoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.sevicer.HoaDonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService hoaDonService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<HoaDonResponseDTO>>> getHoaDons(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer loaiHoaDon,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<HoaDonResponseDTO> hoaDonPage = hoaDonService.getByFilters(
                keyword,
                loaiHoaDon,
                tuNgay,
                denNgay,
                trangThai,
                pageable
        );

        return ResponseEntity.ok(
                new ResponseObject<>(HttpStatus.OK, "Lấy danh sách hóa đơn thành công", hoaDonPage)
        );
    }
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer loaiHoaDon,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay
    ) {
        byte[] excelBytes = hoaDonService.exportExcel(
                keyword,
                loaiHoaDon,
                tuNgay,
                denNgay,
                trangThai
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh-sach-hoa-don.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<HoaDonResponseDTO>> getHoaDonById(@PathVariable Long id) {
        try {
            HoaDonResponseDTO response = hoaDonService.getHoaDonById(id);

            return ResponseEntity.ok(
                    new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết hóa đơn thành công", response)
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<ResponseObject<Void>> updateTrangThai(
            @PathVariable Long id,
            @Valid @RequestBody HoaDonUpdateTrangThaiRequest request
    ) {
        try {
            hoaDonService.updateTrangThai(id, request);

            return ResponseEntity.ok(
                    new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái hóa đơn thành công", null)
            );
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