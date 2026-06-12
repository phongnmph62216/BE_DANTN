package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.CaLamViecRequest;
import com.example.be_dantn.Dto.CaLamViecRespon;
import com.example.be_dantn.Entity.CaLamViec;
import com.example.be_dantn.sevicer.CaLamViecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("api/ca-lam-viec")
public class CaLamViecController {
    private final CaLamViecService  caLamViecService;

    @GetMapping
    public ResponseEntity<List<CaLamViecRespon>> findAll() {
        return ResponseEntity.ok(
                caLamViecService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaLamViecRespon> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                caLamViecService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<CaLamViecRespon> add(
            @RequestBody CaLamViecRequest request
    ) {
        return new ResponseEntity<>(
                caLamViecService.add(request),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaLamViecRespon> update(
            @RequestBody CaLamViecRequest request,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                caLamViecService.update(request, id)
        );
    }

    @PatchMapping("/{id}/doi-trang-thai")
    public ResponseEntity<CaLamViecRespon> doiTrangThai(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                caLamViecService.doiTrangThai(id)
        );
    }
}
