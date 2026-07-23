package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.KichThuocDTO;
import com.example.be_dantn.sevicer.KichThuocService;
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
@RequestMapping("/api/v1/kich-thuoc")
public class KichThuocController {

    private final KichThuocService kichThuocService;

    @GetMapping
    public ResponseEntity<Page<KichThuocDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(kichThuocService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KichThuocDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(kichThuocService.findById(id));
    }

    @PostMapping
    public ResponseEntity<KichThuocDTO> add(@Valid @RequestBody KichThuocDTO kichThuocDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kichThuocService.save(kichThuocDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KichThuocDTO> update(@PathVariable Long id, @Valid @RequestBody KichThuocDTO kichThuocDTO) {
        return ResponseEntity.ok(kichThuocService.update(id, kichThuocDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<KichThuocDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(kichThuocService.toggleStatus(id));
    }
}

