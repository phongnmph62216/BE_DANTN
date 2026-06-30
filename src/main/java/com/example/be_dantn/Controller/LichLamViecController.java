package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.LichLamViecDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.LichLamViecService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/lich-lam-viec")
public class LichLamViecController {

    private final LichLamViecService lichLamViecService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<LichLamViecDTO>>> findAll(
            @RequestParam(required = false) Long idNhanVien,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDate start = null;
        LocalDate end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (startDate != null && !startDate.trim().isEmpty()) {
            start = LocalDate.parse(startDate.trim(), formatter);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            end = LocalDate.parse(endDate.trim(), formatter);
        }

        List<LichLamViecDTO> list = lichLamViecService.findAll(idNhanVien, start, end);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách lịch làm việc thành công", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<LichLamViecDTO>> findById(@PathVariable Long id) {
        LichLamViecDTO dto = lichLamViecService.findById(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết lịch làm việc thành công", dto));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<LichLamViecDTO>> add(@Valid @RequestBody LichLamViecDTO dto) {
        LichLamViecDTO saved = lichLamViecService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject<>(HttpStatus.CREATED, "Lập lịch làm việc thành công", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<LichLamViecDTO>> update(@PathVariable Long id, @Valid @RequestBody LichLamViecDTO dto) {
        LichLamViecDTO updated = lichLamViecService.update(id, dto);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật lịch làm việc thành công", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> delete(@PathVariable Long id) {
        lichLamViecService.delete(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Xóa lịch làm việc thành công", null));
    }
}
