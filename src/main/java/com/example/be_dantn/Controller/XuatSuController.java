package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.XuatSuDTO;
import com.example.be_dantn.sevicer.XuatSuService;
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
@RequestMapping("/api/v1/xuat-su")
public class XuatSuController {

    private final XuatSuService xuatSuService;

    @GetMapping
    public ResponseEntity<Page<XuatSuDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(xuatSuService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<XuatSuDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(xuatSuService.findById(id));
    }

    @PostMapping
    public ResponseEntity<XuatSuDTO> add(@Valid @RequestBody XuatSuDTO xuatSuDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(xuatSuService.save(xuatSuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<XuatSuDTO> update(@PathVariable Long id, @Valid @RequestBody XuatSuDTO xuatSuDTO) {
        return ResponseEntity.ok(xuatSuService.update(id, xuatSuDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<XuatSuDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(xuatSuService.toggleStatus(id));
    }
}

