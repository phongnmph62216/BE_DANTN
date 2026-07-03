package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.LoaiSanPhamDTO;
import com.example.be_dantn.sevicer.LoaiSanPhamService;
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
@RequestMapping("/api/v1/loai-san-pham")
public class LoaiSanPhamController {

    private final LoaiSanPhamService loaiSanPhamService;

    @GetMapping
    public ResponseEntity<Page<LoaiSanPhamDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(loaiSanPhamService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoaiSanPhamDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(loaiSanPhamService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LoaiSanPhamDTO> add(@Valid @RequestBody LoaiSanPhamDTO loaiSanPhamDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loaiSanPhamService.save(loaiSanPhamDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoaiSanPhamDTO> update(@PathVariable Long id, @Valid @RequestBody LoaiSanPhamDTO loaiSanPhamDTO) {
        return ResponseEntity.ok(loaiSanPhamService.update(id, loaiSanPhamDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LoaiSanPhamDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(loaiSanPhamService.toggleStatus(id));
    }
}

