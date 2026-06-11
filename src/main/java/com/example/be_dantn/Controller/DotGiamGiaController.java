package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.DotGiamGiaCreateRequest;
import com.example.be_dantn.Dto.Response.DotGiamGiaResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.DotGiamGiaService;
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
@RequestMapping("/api/v1/dot-giam-gia")
@RequiredArgsConstructor
public class DotGiamGiaController {

    private final DotGiamGiaService dotGiamGiaService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<DotGiamGiaResponseDTO>>> getSales(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DotGiamGiaResponseDTO> salesPage = dotGiamGiaService.getByFilters(keyword, trangThai, tuNgay, denNgay, pageable);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách đợt giảm giá thành công", salesPage));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject<Void>> toggleStatus(@PathVariable Long id) {
        try {
            dotGiamGiaService.toggleStatus(id);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật trạng thái thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseObject<Void>> createDotGiamGia(
            @Valid @RequestBody DotGiamGiaCreateRequest request
    ) {
        try {
            dotGiamGiaService.createDotGiamGia(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject<>(HttpStatus.CREATED, "Tạo đợt giảm giá thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }
}
