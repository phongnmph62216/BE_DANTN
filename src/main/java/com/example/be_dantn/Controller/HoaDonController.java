package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.CapNhatTrangThaiHoaDonRequest;
import com.example.be_dantn.Dto.HoaDonRequest;
import com.example.be_dantn.Dto.HoaDonResponse;
import com.example.be_dantn.sevicer.HoaDonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hoa-don")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HoaDonController {

    private final HoaDonService hoaDonService;

    @GetMapping
    public ResponseEntity<List<HoaDonResponse>> getAll() {
        return ResponseEntity.ok(hoaDonService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<HoaDonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hoaDonService.findById(id));
    }

    @GetMapping("ma/{maHoaDon}")
    public ResponseEntity<HoaDonResponse> getByMaHoaDon(@PathVariable String maHoaDon) {
        return ResponseEntity.ok(hoaDonService.findByMaHoaDon(maHoaDon));
    }

    @PostMapping
    public ResponseEntity<HoaDonResponse> add(@Valid @RequestBody HoaDonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hoaDonService.add(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<HoaDonResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody HoaDonRequest request
    ) {
        return ResponseEntity.ok(hoaDonService.update(request, id));
    }

    @PutMapping("{id}/trang-thai")
    public ResponseEntity<HoaDonResponse> capNhatTrangThai(
            @PathVariable Long id,
            @Valid @RequestBody CapNhatTrangThaiHoaDonRequest request
    ) {
        return ResponseEntity.ok(hoaDonService.capNhatTrangThai(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hoaDonService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        byte[] data = hoaDonService.exportExcel();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=hoa-don.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }
    @PutMapping("{id}/huy")
    public ResponseEntity<HoaDonResponse> huyHoaDon(
            @PathVariable Long id,
            @RequestBody(required = false) CapNhatTrangThaiHoaDonRequest request
    ) {
        return ResponseEntity.ok(hoaDonService.huyHoaDon(id, request));
    }
}