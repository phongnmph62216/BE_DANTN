package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.HoaDonResponseDTO;
import com.example.be_dantn.Dto.DonHangChoResponseDTO;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Dto.Request.ThongTinNhanHangRequestDTO;
import com.example.be_dantn.Dto.Request.ThemSanPhamRequest;
import com.example.be_dantn.Dto.Request.CapNhatSoLuongRequest;
import com.example.be_dantn.Dto.Request.ApDungVoucherRequest;
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
    public ResponseEntity<ResponseObject<HoaDonResponseDTO>> taoDonHangCho(
            @RequestParam(required = false, defaultValue = "0") Integer loaiHoaDon) {
        HoaDonResponseDTO hoaDonMoi = banHangService.taoDonHangCho(loaiHoaDon);
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

    @GetMapping("/don-hang/{orderId}")
    public ResponseEntity<ResponseObject<Object>> layChiTietDonHang(@PathVariable Long orderId) {
        Object data = banHangService.layChiTietDonHang(orderId);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy chi tiết đơn hàng thành công", data));
    }

    @PutMapping("/don-hang/{idHoaDon}/khach-hang")
    public ResponseEntity<ResponseObject<Void>> capNhatKhachHang(
            @PathVariable Long idHoaDon,
            @RequestParam(required = false) Long idKhachHang) {
        banHangService.capNhatKhachHang(idHoaDon, idKhachHang);
        return ResponseEntity.ok(new ResponseObject<>("success", "Cập nhật khách hàng thành công", null));
    }

    @PostMapping("/don-hang/{idHoaDon}/them-san-pham")
    public ResponseEntity<ResponseObject<Void>> themSanPham(
            @PathVariable Long idHoaDon,
            @RequestBody ThemSanPhamRequest request) {
        banHangService.themSanPhamVaoHoaDon(idHoaDon, request.getIdChiTietSanPham(), request.getSoLuong());
        return ResponseEntity.ok(new ResponseObject<>("success", "Thêm sản phẩm thành công", null));
    }

    @PutMapping("/chi-tiet/{idHoaDonChiTiet}")
    public ResponseEntity<ResponseObject<Void>> capNhatSoLuong(
            @PathVariable Long idHoaDonChiTiet,
            @RequestBody CapNhatSoLuongRequest request) {
        banHangService.capNhatSoLuongSanPham(idHoaDonChiTiet, request.getSoLuong());
        return ResponseEntity.ok(new ResponseObject<>("success", "Cập nhật số lượng thành công", null));
    }

    @DeleteMapping("/chi-tiet/{idHoaDonChiTiet}")
    public ResponseEntity<ResponseObject<Void>> xoaSanPham(@PathVariable Long idHoaDonChiTiet) {
        banHangService.xoaSanPhamKhoiHoaDon(idHoaDonChiTiet);
        return ResponseEntity.ok(new ResponseObject<>("success", "Xóa sản phẩm thành công", null));
    }

    @PostMapping("/don-hang/{idHoaDon}/voucher")
    public ResponseEntity<ResponseObject<Void>> apDungVoucher(
            @PathVariable Long idHoaDon,
            @RequestBody ApDungVoucherRequest request) {
        banHangService.apDungVoucher(idHoaDon, request.getMaPhieuGiamGia());
        return ResponseEntity.ok(new ResponseObject<>("success", "Áp dụng voucher thành công", null));
    }

    @DeleteMapping("/don-hang/{idHoaDon}/voucher")
    public ResponseEntity<ResponseObject<Void>> xoaVoucher(@PathVariable Long idHoaDon) {
        banHangService.xoaVoucher(idHoaDon);
        return ResponseEntity.ok(new ResponseObject<>("success", "Gỡ bỏ voucher thành công", null));
    }

    @PostMapping("/don-hang/{orderId}/yeu-cau-huy")
    public ResponseEntity<ResponseObject<Void>> yeuCauHuyDon(
            @PathVariable Long orderId,
            @RequestParam(required = false, defaultValue = "") String ghiChu) {
        banHangService.yeuCauHuyDon(orderId, ghiChu);
        return ResponseEntity.ok(new ResponseObject<>("success", "Gửi yêu cầu hủy đơn thành công", null));
    }

    @PutMapping("/don-hang/{orderId}/phe-duyet-huy")
    public ResponseEntity<ResponseObject<Void>> pheDuyetHuyDon(
            @PathVariable Long orderId,
            @RequestParam Boolean dongY,
            @RequestParam(required = false, defaultValue = "") String ghiChu) {
        banHangService.pheDuyetHuyDon(orderId, dongY, ghiChu);
        return ResponseEntity.ok(new ResponseObject<>("success", "Xử lý yêu cầu hủy đơn thành công", null));
    }
}
