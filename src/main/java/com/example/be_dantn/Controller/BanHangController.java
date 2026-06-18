package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.DonHangChoResponseDTO;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Dto.Request.ThongTinNhanHangRequestDTO;
import com.example.be_dantn.Dto.ResponseObject;
import com.example.be_dantn.sevicer.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ban-hang")
public class BanHangController {

    @Autowired
    private BanHangService banHangService;

    @GetMapping("/don-hang-cho")
    public ResponseEntity<ResponseObject<List<DonHangChoResponseDTO>>> layDanhSachDonHangCho() {
        List<DonHangChoResponseDTO> list = banHangService.layDanhSachDonHangCho();
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy danh sách đơn hàng chờ thành công", list));
    }

    @PostMapping("/tao-don")
    public ResponseEntity<ResponseObject<HoaDonResponseDTO>> taoDonHangCho() {
        HoaDonResponseDTO hoaDonMoi = banHangService.taoDonHangCho();
        return ResponseEntity.ok(new ResponseObject<>("success", "Tạo đơn hàng chờ thành công", hoaDonMoi));
    }

    @PutMapping("/{idHoaDon}/thong-tin-nhan-hang")
    public ResponseEntity<ResponseObject<Void>> capNhatThongTin(
            @PathVariable Long idHoaDon,
            @RequestBody ThongTinNhanHangRequestDTO request) {
        banHangService.capNhatThongTinNhanHang(idHoaDon, request);
        return ResponseEntity.ok(new ResponseObject<>("success", "Cập nhật thông tin thành công", null));
    }

    @PostMapping("/{idHoaDon}/thanh-toan")
    public ResponseEntity<ResponseObject<Long>> chotDonThanhToan(
            @PathVariable Long idHoaDon,
            @RequestBody ThanhToanRequestDTO request) {
        Long finalId = banHangService.thanhToanHoaDon(idHoaDon, request);
        return ResponseEntity.ok(new ResponseObject<>("success", "Thanh toán và chốt đơn thành công", finalId));
    }
}
