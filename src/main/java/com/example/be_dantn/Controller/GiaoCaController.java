package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.GiaoCaDTO;
import com.example.be_dantn.Dto.GiaoCaStatusDTO;
import com.example.be_dantn.Dto.Request.MoCaRequest;
import com.example.be_dantn.Dto.Request.ChotCaRequest;
import com.example.be_dantn.Dto.Request.DoiSoatRequest;
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

    @GetMapping("/current-status")
    public ResponseEntity<ResponseObject<GiaoCaStatusDTO>> getShiftStatus(
            @RequestHeader(value = "X-Employee-Id", required = false) Long employeeId) {
        GiaoCaStatusDTO dto = giaoCaService.getShiftStatus(employeeId);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy trạng thái giao ca hiện tại thành công", dto));
    }

    @PostMapping("/mo-ca")
    public ResponseEntity<ResponseObject<GiaoCaDTO>> moCa(
            @RequestBody MoCaRequest request) {
        GiaoCaDTO dto = giaoCaService.moCa(request);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Mở ca làm việc thành công", dto));
    }

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

    @PostMapping("/chot-ca")
    public ResponseEntity<ResponseObject<GiaoCaDTO>> chotCa(@RequestBody ChotCaRequest request) {
        GiaoCaDTO dto = giaoCaService.chotCa(request);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Chốt ca làm việc thành công", dto));
    }

    @PutMapping("/doi-soat/{id}")
    public ResponseEntity<ResponseObject<GiaoCaDTO>> doiSoat(
            @PathVariable Long id,
            @RequestBody DoiSoatRequest request) {
        GiaoCaDTO dto = giaoCaService.doiSoat(id, request);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Đối soát ca trực thành công", dto));
    }
}
