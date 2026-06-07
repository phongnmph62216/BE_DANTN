package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.LichSuHoaDonResponse;
import com.example.be_dantn.sevicer.LichSuHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lich-su-hoa-don")
@RequiredArgsConstructor
public class LichSuHoaDonController {

    private final LichSuHoaDonService lichSuHoaDonService;

    @GetMapping("/hoa-don/{idHoaDon}")
    public List<LichSuHoaDonResponse> getByHoaDon(@PathVariable Long idHoaDon) {
        return lichSuHoaDonService.getByHoaDon(idHoaDon);
    }
}