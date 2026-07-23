package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.KieuDangDTO;
import com.example.be_dantn.sevicer.KieuDangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/kieu-dang")
public class KieuDangController {

    private final KieuDangService kieuDangService;

    @GetMapping
    public ResponseEntity<Page<KieuDangDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(kieuDangService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KieuDangDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(kieuDangService.findById(id));
    }

    @PostMapping
    public ResponseEntity<KieuDangDTO> add(@Valid @RequestBody KieuDangDTO kieuDangDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kieuDangService.save(kieuDangDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KieuDangDTO> update(@PathVariable Long id, @Valid @RequestBody KieuDangDTO kieuDangDTO) {
        return ResponseEntity.ok(kieuDangService.update(id, kieuDangDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<KieuDangDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(kieuDangService.toggleStatus(id));
    }
}

