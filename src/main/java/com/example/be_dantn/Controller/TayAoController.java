package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.TayAoDTO;
import com.example.be_dantn.sevicer.TayAoService;
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
@RequestMapping("/api/v1/tay-ao")
public class TayAoController {

    private final TayAoService tayAoService;

    @GetMapping
    public ResponseEntity<Page<TayAoDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer trangThai) {
        return ResponseEntity.ok(tayAoService.findAll(pageable, keyword, trangThai));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TayAoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tayAoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TayAoDTO> add(@Valid @RequestBody TayAoDTO tayAoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tayAoService.save(tayAoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TayAoDTO> update(@PathVariable Long id, @Valid @RequestBody TayAoDTO tayAoDTO) {
        return ResponseEntity.ok(tayAoService.update(id, tayAoDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TayAoDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(tayAoService.toggleStatus(id));
    }
}

