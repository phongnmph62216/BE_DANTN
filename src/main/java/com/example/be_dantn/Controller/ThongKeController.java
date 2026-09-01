package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.ResponseObject;
import com.example.be_dantn.sevicer.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/thong-ke")
public class ThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @Autowired
    private com.example.be_dantn.sevicer.EmailService emailService;

    @GetMapping("/tong-quan")
    public ResponseEntity<ResponseObject<Map<String, Object>>> getTongQuan() {
        Map<String, Object> data = thongKeService.getTongQuan();
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy thống kê tổng quan thành công", data));
    }

    @GetMapping("/doanh-thu-bieu-do")
    public ResponseEntity<ResponseObject<List<Map<String, Object>>>> getDoanhThuBieuDo(
            @RequestParam(defaultValue = "year") String type,
            @RequestParam(required = false) String valueA,
            @RequestParam(required = false) String valueB
    ) {
        if (valueA == null || valueA.trim().isEmpty()) {
            if ("year".equals(type)) {
                valueA = String.valueOf(LocalDate.now().getYear());
            } else if ("month".equals(type)) {
                valueA = String.format("%d-%02d", LocalDate.now().getYear(), LocalDate.now().getMonthValue());
            } else {
                valueA = LocalDate.now().toString();
            }
        }
        List<Map<String, Object>> data = thongKeService.getDoanhThuBieuDo(type, valueA, valueB);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy thống kê doanh thu biểu đồ thành công", data));
    }

    @GetMapping("/chi-tiet")
    public ResponseEntity<ResponseObject<Map<String, Object>>> getChiTietThongKe(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay,
            @RequestParam(required = false, defaultValue = "00:00:00") String tuGio,
            @RequestParam(required = false, defaultValue = "23:59:59") String denGio
    ) {
        if (tuNgay == null) {
            // Default to 30 days ago
            tuNgay = LocalDate.now().minusDays(30).atStartOfDay();
        }
        if (denNgay == null) {
            // Default to end of today
            denNgay = LocalDate.now().atTime(LocalTime.MAX);
        }
        Map<String, Object> data = thongKeService.getChiTietThongKe(tuNgay, denNgay, tuGio, denGio);
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy thống kê chi tiết thành công", data));
    }

    @PostMapping("/send-email-report")
    public ResponseEntity<ResponseObject<String>> sendEmailReport(@RequestBody Map<String, Object> req) {
        String email = (String) req.get("email");
        String type = (String) req.getOrDefault("type", "today");
        java.math.BigDecimal doanhThu = req.get("doanhThu") != null ? new java.math.BigDecimal(req.get("doanhThu").toString()) : java.math.BigDecimal.ZERO;
        Long soDonHang = req.get("soDonHang") != null ? Long.parseLong(req.get("soDonHang").toString()) : 0L;
        Long hoanThanh = req.get("hoanThanh") != null ? Long.parseLong(req.get("hoanThanh").toString()) : 0L;
        Long soSanPham = req.get("soSanPham") != null ? Long.parseLong(req.get("soSanPham").toString()) : 0L;
        java.math.BigDecimal tienMat = req.get("tienMat") != null ? new java.math.BigDecimal(req.get("tienMat").toString()) : java.math.BigDecimal.ZERO;
        java.math.BigDecimal chuyenKhoan = req.get("chuyenKhoan") != null ? new java.math.BigDecimal(req.get("chuyenKhoan").toString()) : java.math.BigDecimal.ZERO;
        java.math.BigDecimal vnpay = req.get("vnpay") != null ? new java.math.BigDecimal(req.get("vnpay").toString()) : java.math.BigDecimal.ZERO;

        emailService.sendRevenueReportEmail(email, type, doanhThu, soDonHang, hoanThanh, soSanPham, tienMat, chuyenKhoan, vnpay);
        return ResponseEntity.ok(new ResponseObject<>("success", "Gửi email báo cáo thành công", "Sent"));
    }
}
