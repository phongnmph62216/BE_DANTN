package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.ThuongHieuDTO;
import com.example.be_dantn.sevicer.ThuongHieuService;
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
@RequestMapping("/api/v1/thuong-hieu")
public class ThuongHieuController {

    private final ThuongHieuService thuongHieuService;

    @GetMapping
    public ResponseEntity<Page<ThuongHieuDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(thuongHieuService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieuDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(thuongHieuService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ThuongHieuDTO> add(@Valid @RequestBody ThuongHieuDTO thuongHieuDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(thuongHieuService.save(thuongHieuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThuongHieuDTO> update(@PathVariable Long id, @Valid @RequestBody ThuongHieuDTO thuongHieuDTO) {
        return ResponseEntity.ok(thuongHieuService.update(id, thuongHieuDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ThuongHieuDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(thuongHieuService.toggleStatus(id));
    }
}

