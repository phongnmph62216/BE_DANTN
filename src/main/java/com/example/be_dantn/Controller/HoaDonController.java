package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.Request.UpdateTrangThaiRequest;
import com.example.be_dantn.Dto.Response.HoaDonDetailResponseDTO;
import com.example.be_dantn.Dto.Response.LichSuHoaDonResponseDTO;
import com.example.be_dantn.Dto.ResponseObject;
import com.example.be_dantn.sevicer.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hoa-don")
public class HoaDonController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping
    public ResponseEntity<ResponseObject<Page<HoaDonResponseDTO>>> getDanhSachHoaDon(
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(required = false) Integer loaiDon,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<HoaDonResponseDTO> danhSachHoaDon = hoaDonService.layDanhSachHoaDon(maHoaDon, tuNgay, denNgay, loaiDon, trangThai, page, size);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy danh sách hóa đơn thành công", danhSachHoaDon));
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> xuatExcelDanhSachHoaDon(
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(required = false) Integer loaiDon,
            @RequestParam(required = false) Integer trangThai
    ) {
        byte[] excelData = hoaDonService.xuatExcelDanhSachHoaDon(maHoaDon, tuNgay, denNgay, loaiDon, trangThai);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "danh_sach_hoa_don.xlsx");

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<HoaDonDetailResponseDTO>> getHoaDonDetail(@PathVariable Long id) {
        HoaDonDetailResponseDTO hoaDonDetail = hoaDonService.layChiTietHoaDon(id);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy chi tiết hóa đơn thành công", hoaDonDetail));
    }

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<ResponseObject<Void>> updateTrangThai(
            @PathVariable Long id,
            @RequestBody UpdateTrangThaiRequest request
    ) {
        hoaDonService.capNhatTrangThaiHoaDon(id, request.getTrangThaiMoi(), request.getGhiChu());
        return ResponseEntity.ok(new ResponseObject<>("success", "Cập nhật trạng thái thành công", null));
    }

    @GetMapping("/{id}/lich-su")
    public ResponseEntity<ResponseObject<List<LichSuHoaDonResponseDTO>>> getLichSuHoaDon(@PathVariable Long id) {
        List<LichSuHoaDonResponseDTO> lichSu = hoaDonService.layLichSuHoaDon(id);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy lịch sử hóa đơn thành công", lichSu));
    }
}
