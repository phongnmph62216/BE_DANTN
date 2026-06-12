package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.LichLamViecRequest;
import com.example.be_dantn.Dto.LichLamViecResponse;
import com.example.be_dantn.sevicer.LichLamViecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lich-lam-viec")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LichLamViecController {

    private final LichLamViecService lichLamViecService;

    @GetMapping
    public ResponseEntity<List<LichLamViecResponse>> getAll() {

        return ResponseEntity.ok(
                lichLamViecService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LichLamViecResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                lichLamViecService.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<LichLamViecResponse> create(
            @RequestBody LichLamViecRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        lichLamViecService.add(request)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LichLamViecResponse> update(
            @RequestBody LichLamViecRequest request, @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                lichLamViecService.update( request,id)
        );
    }

    @PatchMapping("/{id}/doi-trang-thai")
    public ResponseEntity<LichLamViecResponse> doiTrangThai(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                lichLamViecService.doiTrangThai(id)
        );
    }
}
