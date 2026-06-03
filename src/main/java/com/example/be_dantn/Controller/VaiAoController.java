package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.VaiAoDTO;
import com.example.be_dantn.sevicer.VaiAoService;
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
@RequestMapping("/api/v1/vai-ao")
public class VaiAoController {

    private final VaiAoService vaiAoService;

    @GetMapping
    public ResponseEntity<Page<VaiAoDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(vaiAoService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VaiAoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vaiAoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VaiAoDTO> add(@Valid @RequestBody VaiAoDTO vaiAoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vaiAoService.save(vaiAoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VaiAoDTO> update(@PathVariable Long id, @Valid @RequestBody VaiAoDTO vaiAoDTO) {
        return ResponseEntity.ok(vaiAoService.update(id, vaiAoDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VaiAoDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(vaiAoService.toggleStatus(id));
    }
}

