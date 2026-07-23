package com.example.be_dantn.Scheduler;

import com.example.be_dantn.Entity.HoaDon;
import com.example.be_dantn.repository.HoaDonRepository;
import com.example.be_dantn.sevicer.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DonHangRacCleanupScheduler {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private BanHangService banHangService;

    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    public void cleanupExpiredVnpayOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
        List<HoaDon> expiredOrders = hoaDonRepository.findExpiredVnpayOrders(threshold);
        for (HoaDon hd : expiredOrders) {
            try {
                banHangService.huyHoaDonOnlineThatBai(hd.getId(), "Tự động hủy đơn hàng do quá 15 phút chưa hoàn thành thanh toán VNPAY.");
            } catch (Exception e) {
                System.err.println("Lỗi khi tự động hủy hóa đơn hết hạn: " + hd.getMaHoaDon() + " - " + e.getMessage());
            }
        }
    }
}
