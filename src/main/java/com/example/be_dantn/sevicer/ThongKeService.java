package com.example.be_dantn.sevicer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ThongKeService {
    Map<String, Object> getTongQuan();
    List<Map<String, Object>> getDoanhThuBieuDo(String type, String valueA, String valueB);
    Map<String, Object> getChiTietThongKe(LocalDateTime tuNgay, LocalDateTime denNgay, String tuGio, String denGio);
}
