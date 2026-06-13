package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.LichSuHoaDonCreateRequest;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.sevicer.LichSuHoaDonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lich-su-hoa-don")
@RequiredArgsConstructor
public class LichSuHoaDonController {

    private final LichSuHoaDonService lichSuHoaDonService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<LichSuHoaDonResponseDTO>>> getByHoaDonId(
            @RequestParam Long hoaDonId
    ) {
        try {
            List<LichSuHoaDonResponseDTO> response = lichSuHoaDonService.getByHoaDonId(hoaDonId);

            return ResponseEntity.ok(
                    new ResponseObject<>(HttpStatus.OK, "Lấy lịch sử hóa đơn thành công", response)
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseObject<LichSuHoaDonResponseDTO>> create(
            @Valid @RequestBody LichSuHoaDonCreateRequest request
    ) {
        try {
            LichSuHoaDonResponseDTO response = lichSuHoaDonService.create(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject<>(HttpStatus.CREATED, "Thêm lịch sử hóa đơn thành công", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }
}