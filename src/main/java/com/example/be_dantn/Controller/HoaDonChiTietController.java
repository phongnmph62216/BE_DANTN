package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.HoaDonChiTietRequest;
import com.example.be_dantn.Dto.HoaDonChiTietResponse;
import com.example.be_dantn.sevicer.HoaDonChiTietService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hoa-don-chi-tiet")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HoaDonChiTietController {

    private final HoaDonChiTietService hoaDonChiTietService;

    @GetMapping
    public ResponseEntity<List<HoaDonChiTietResponse>> getAll() {
        return ResponseEntity.ok(hoaDonChiTietService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<HoaDonChiTietResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hoaDonChiTietService.findById(id));
    }

    @GetMapping("hoa-don/{idHoaDon}")
    public ResponseEntity<List<HoaDonChiTietResponse>> getByHoaDon(@PathVariable Long idHoaDon) {
        return ResponseEntity.ok(hoaDonChiTietService.findByHoaDon(idHoaDon));
    }

    @PostMapping
    public ResponseEntity<HoaDonChiTietResponse> add(@Valid @RequestBody HoaDonChiTietRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hoaDonChiTietService.add(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<HoaDonChiTietResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HoaDonChiTietRequest request
    ) {
        return ResponseEntity.ok(hoaDonChiTietService.update(request, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hoaDonChiTietService.delete(id);
        return ResponseEntity.noContent().build();
    }
}