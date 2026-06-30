package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.CaLamViecDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.CaLamViecService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ca-lam-viec")
public class CaLamViecController {

    private final CaLamViecService caLamViecService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<CaLamViecDTO>>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer trangThai) {

        LocalTime start = null;
        LocalTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        if (startTime != null && !startTime.trim().isEmpty()) {
            start = LocalTime.parse(startTime.trim(), formatter);
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            end = LocalTime.parse(endTime.trim(), formatter);
        }

        List<CaLamViecDTO> list = caLamViecService.findAll(keyword, start, end, trangThai);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách ca làm việc thành công", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<CaLamViecDTO>> findById(@PathVariable Long id) {
        CaLamViecDTO dto = caLamViecService.findById(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết ca làm việc thành công", dto));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<CaLamViecDTO>> add(@Valid @RequestBody CaLamViecDTO dto) {
        CaLamViecDTO saved = caLamViecService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject<>(HttpStatus.CREATED, "Thêm ca làm việc thành công", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<CaLamViecDTO>> update(@PathVariable Long id, @Valid @RequestBody CaLamViecDTO dto) {
        CaLamViecDTO updated = caLamViecService.update(id, dto);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Cập nhật ca làm việc thành công", updated));
    }

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<ResponseObject<CaLamViecDTO>> toggleStatus(@PathVariable Long id) {
        CaLamViecDTO toggled = caLamViecService.toggleStatus(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Thay đổi trạng thái ca làm việc thành công", toggled));
    }
}
