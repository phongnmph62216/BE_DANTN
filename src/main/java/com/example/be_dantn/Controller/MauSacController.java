package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.MauSacDTO;
import com.example.be_dantn.sevicer.MauSacService;
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
@RequestMapping("/api/v1/mau-sac")
public class MauSacController {

    private final MauSacService mauSacService;

    @GetMapping
    public ResponseEntity<Page<MauSacDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(mauSacService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MauSacDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mauSacService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MauSacDTO> add(@Valid @RequestBody MauSacDTO mauSacDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mauSacService.save(mauSacDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MauSacDTO> update(@PathVariable Long id, @Valid @RequestBody MauSacDTO mauSacDTO) {
        return ResponseEntity.ok(mauSacService.update(id, mauSacDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MauSacDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(mauSacService.toggleStatus(id));
    }
}

