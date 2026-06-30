package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.GiaoCaDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.GiaoCaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/giao-ca")
public class GiaoCaController {

    private final GiaoCaService giaoCaService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<GiaoCaDTO>>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        LocalDateTime from = null;
        LocalDateTime to = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                String val = fromDate.trim();
                if (val.contains(" ")) {
                    from = LocalDateTime.parse(val, dateTimeFormatter);
                } else {
                    from = LocalDate.parse(val).atStartOfDay();
                }
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                String val = toDate.trim();
                if (val.contains(" ")) {
                    to = LocalDateTime.parse(val, dateTimeFormatter);
                } else {
                    to = LocalDate.parse(val).atTime(23, 59, 59);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject<>(HttpStatus.BAD_REQUEST, "Định dạng ngày không hợp lệ. Sử dụng yyyy-MM-dd hoặc yyyy-MM-dd HH:mm:ss", null));
        }

        List<GiaoCaDTO> list = giaoCaService.findAll(keyword, from, to);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách giao ca thành công", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<GiaoCaDTO>> findById(@PathVariable Long id) {
        GiaoCaDTO dto = giaoCaService.findById(id);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy thông tin chi tiết giao ca thành công", dto));
    }
}
