package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Response.HoaDonChiTietResponseDTO;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Exception.ResourceNotFoundException;
import com.example.be_dantn.sevicer.HoaDonChiTietService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hoa-don-chi-tiet")
@RequiredArgsConstructor
public class HoaDonChiTietController {

    private final HoaDonChiTietService hoaDonChiTietService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<HoaDonChiTietResponseDTO>>> getByHoaDonId(
            @RequestParam Long hoaDonId
    ) {
        try {
            List<HoaDonChiTietResponseDTO> response = hoaDonChiTietService.getByHoaDonId(hoaDonId);

            return ResponseEntity.ok(
                    new ResponseObject<>(HttpStatus.OK, "Lấy chi tiết sản phẩm trong hóa đơn thành công", response)
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject<>(HttpStatus.NOT_FOUND, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage(), null));
        }
    }
}