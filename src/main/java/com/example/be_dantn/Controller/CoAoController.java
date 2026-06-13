package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.CoAoDTO;
import com.example.be_dantn.sevicer.CoAoService;
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
@RequestMapping("/api/v1/co-ao")
public class CoAoController {

    private final CoAoService coAoService;

    @GetMapping
    public ResponseEntity<Page<CoAoDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(coAoService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoAoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(coAoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CoAoDTO> add(@Valid @RequestBody CoAoDTO coAoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coAoService.save(coAoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoAoDTO> update(@PathVariable Long id, @Valid @RequestBody CoAoDTO coAoDTO) {
        return ResponseEntity.ok(coAoService.update(id, coAoDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CoAoDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(coAoService.toggleStatus(id));
    }
}

