package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.ResponseObject;
import com.example.be_dantn.Dto.Request.ThanhToanRequestDTO;
import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.repository.HoaDonRepository;
import com.example.be_dantn.sevicer.BanHangService;
import com.example.be_dantn.sevicer.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private BanHangService banHangService;

    /**
     * Tạo URL thanh toán VNPAY
     * Frontend gọi API này, nhận URL rồi redirect sang VNPAY sandbox
     */
    @PostMapping("/create-payment")
    public ResponseEntity<ResponseObject<Map<String, String>>> createPayment(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        long amount = Long.parseLong(request.get("amount").toString());
        String orderInfo = request.getOrDefault("orderInfo", "Thanh toan don hang").toString();
        String orderId = request.getOrDefault("orderId", String.valueOf(System.currentTimeMillis())).toString();

        // Lấy IP khách hàng
        String ipAddr = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddr == null || ipAddr.isEmpty()) {
            ipAddr = httpRequest.getRemoteAddr();
        }

        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, orderId, ipAddr);

        Map<String, String> result = new HashMap<>();
        result.put("paymentUrl", paymentUrl);

        return ResponseEntity.ok(new ResponseObject<>("success", "Tạo URL thanh toán thành công", result));
    }

    /**
     * Xử lý kết quả VNPAY trả về (Return URL)
     * VNPAY redirect trình duyệt khách hàng về đây
     * Sau khi verify, redirect sang frontend hiển thị kết quả
     */
    @GetMapping("/return")
    public ResponseEntity<ResponseObject<Map<String, String>>> vnpayReturn(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        boolean isValid = vnPayService.verifyReturn(params);
        String responseCode = params.getOrDefault("vnp_ResponseCode", "99");
        String txnRef = params.getOrDefault("vnp_TxnRef", "");
        String amount = params.getOrDefault("vnp_Amount", "0");
        String orderInfo = params.getOrDefault("vnp_OrderInfo", "");
        String transactionNo = params.getOrDefault("vnp_TransactionNo", "");

        // Tách lấy mã hóa đơn gốc (ví dụ: HD006_1719321495 -> HD006)
        String maHoaDon = txnRef;
        if (txnRef.contains("_")) {
            maHoaDon = txnRef.split("_")[0];
        }

        boolean isSuccess = isValid && "00".equals(responseCode);

        // Nếu thanh toán thành công, tiến hành chốt hóa đơn trên DB
        if (isSuccess && !maHoaDon.isEmpty()) {
            Optional<HoaDon> hoaDonOpt = hoaDonRepository.findByMaHoaDon(maHoaDon);
            if (hoaDonOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();
                if (hoaDon.getTrangThai() == 0) { // Chỉ chốt nếu hóa đơn đang ở trạng thái chờ thanh toán
                    ThanhToanRequestDTO payReq = new ThanhToanRequestDTO();
                    payReq.setTienMat(BigDecimal.ZERO);
                    payReq.setTienChuyenKhoan(hoaDon.getTongTienThanhToan());
                    payReq.setGhiChu("Thanh toán online qua VNPAY. Mã GD: " + transactionNo);
                    banHangService.thanhToanHoaDon(hoaDon.getId(), payReq);
                }
            }
        }

        Map<String, String> result = new HashMap<>();
        result.put("isValid", String.valueOf(isValid));
        result.put("responseCode", responseCode);
        result.put("txnRef", maHoaDon); // Trả về mã đơn hàng gốc để hiển thị trên FE
        result.put("amount", String.valueOf(Long.parseLong(amount) / 100)); // Chia lại 100
        result.put("orderInfo", orderInfo);
        result.put("transactionNo", transactionNo);
        result.put("isSuccess", String.valueOf(isSuccess));

        return ResponseEntity.ok(new ResponseObject<>("success", "Kết quả thanh toán", result));
    }
}
