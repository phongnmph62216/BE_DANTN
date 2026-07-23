package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.sevicer.ThongKeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTongQuan() {
        LocalDateTime now = LocalDateTime.now();
        
        // Today
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        
        // This Week
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDateTime startOfWeek = monday.atStartOfDay();
        LocalDateTime endOfWeek = today.atTime(LocalTime.MAX);
        
        // This Month
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = today.atTime(LocalTime.MAX);
        
        // This Year
        LocalDate firstDayOfYear = today.withDayOfYear(1);
        LocalDateTime startOfYear = firstDayOfYear.atStartOfDay();
        LocalDateTime endOfYear = today.atTime(LocalTime.MAX);

        Map<String, Object> result = new HashMap<>();
        result.put("homNay", getPeriodStats(startOfToday, endOfToday));
        result.put("tuanNay", getPeriodStats(startOfWeek, endOfWeek));
        result.put("thangNay", getPeriodStats(startOfMonth, endOfMonth));
        result.put("namNay", getPeriodStats(startOfYear, endOfYear));
        return result;
    }

    private Map<String, Object> getPeriodStats(LocalDateTime start, LocalDateTime end) {
        // 1. Calculate revenue
        String sqlRevenue = "SELECT SUM(tong_tien_thanh_toan) FROM hoa_don WHERE trang_thai = 4 AND ngay_tao BETWEEN :start AND :end";
        Query qRevenue = entityManager.createNativeQuery(sqlRevenue);
        qRevenue.setParameter("start", start);
        qRevenue.setParameter("end", end);
        Object resRevenue = qRevenue.getSingleResult();
        BigDecimal revenue = resRevenue != null ? new BigDecimal(resRevenue.toString()) : BigDecimal.ZERO;

        // 2. Calculate products sold
        String sqlSold = "SELECT SUM(hdct.so_luong) FROM hoa_don_chi_tiet hdct JOIN hoa_don hd ON hdct.id_hoa_don = hd.id WHERE hd.trang_thai = 4 AND hd.ngay_tao BETWEEN :start AND :end";
        Query qSold = entityManager.createNativeQuery(sqlSold);
        qSold.setParameter("start", start);
        qSold.setParameter("end", end);
        Object resSold = qSold.getSingleResult();
        Long productsSold = resSold != null ? Long.parseLong(resSold.toString()) : 0L;

        // 3. Count total orders
        String sqlOrders = "SELECT COUNT(id) FROM hoa_don WHERE ngay_tao BETWEEN :start AND :end";
        Query qOrders = entityManager.createNativeQuery(sqlOrders);
        qOrders.setParameter("start", start);
        qOrders.setParameter("end", end);
        Object resOrders = qOrders.getSingleResult();
        Long totalOrders = resOrders != null ? Long.parseLong(resOrders.toString()) : 0L;

        // 4. Completed orders
        String sqlCompleted = "SELECT COUNT(id) FROM hoa_don WHERE trang_thai = 4 AND ngay_tao BETWEEN :start AND :end";
        Query qCompleted = entityManager.createNativeQuery(sqlCompleted);
        qCompleted.setParameter("start", start);
        qCompleted.setParameter("end", end);
        Object resCompleted = qCompleted.getSingleResult();
        Long completed = resCompleted != null ? Long.parseLong(resCompleted.toString()) : 0L;

        // 5. Cancelled orders
        String sqlCancelled = "SELECT COUNT(id) FROM hoa_don WHERE trang_thai = 5 AND ngay_tao BETWEEN :start AND :end";
        Query qCancelled = entityManager.createNativeQuery(sqlCancelled);
        qCancelled.setParameter("start", start);
        qCancelled.setParameter("end", end);
        Object resCancelled = qCancelled.getSingleResult();
        Long cancelled = resCancelled != null ? Long.parseLong(resCancelled.toString()) : 0L;

        // 6. Processing orders (statuses 0, 1, 2, 3)
        String sqlProcessing = "SELECT COUNT(id) FROM hoa_don WHERE trang_thai IN (0, 1, 2, 3) AND ngay_tao BETWEEN :start AND :end";
        Query qProcessing = entityManager.createNativeQuery(sqlProcessing);
        qProcessing.setParameter("start", start);
        qProcessing.setParameter("end", end);
        Object resProcessing = qProcessing.getSingleResult();
        Long processing = resProcessing != null ? Long.parseLong(resProcessing.toString()) : 0L;

        Map<String, Object> map = new HashMap<>();
        map.put("doanhThu", revenue);
        map.put("soSanPhamDaBan", productsSold);
        map.put("soDonHang", totalOrders);
        map.put("hoanThanh", completed);
        map.put("huy", cancelled);
        map.put("dangXuLy", processing);
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDoanhThuBieuDo(String type, String valueA, String valueB) {
        List<Map<String, Object>> list = new ArrayList<>();
        
        if ("year".equals(type)) {
            int yearA = Integer.parseInt(valueA);
            Integer yearB = (valueB != null && !valueB.trim().isEmpty()) ? Integer.parseInt(valueB) : null;
            
            Map<Integer, BigDecimal> mapA = getYearlyRevenue(yearA);
            Map<Integer, BigDecimal> mapB = yearB != null ? getYearlyRevenue(yearB) : null;
            
            for (int m = 1; m <= 12; m++) {
                Map<String, Object> itemA = new HashMap<>();
                itemA.put("label", "T" + m);
                itemA.put("value", mapA.getOrDefault(m, BigDecimal.ZERO));
                itemA.put("type", "Doanh thu (" + yearA + ")");
                list.add(itemA);
                
                if (mapB != null) {
                    Map<String, Object> itemB = new HashMap<>();
                    itemB.put("label", "T" + m);
                    itemB.put("value", mapB.getOrDefault(m, BigDecimal.ZERO));
                    itemB.put("type", "So sánh (" + yearB + ")");
                    list.add(itemB);
                }
            }
        } else if ("month".equals(type)) {
            String[] partsA = parseMonthStr(valueA);
            int yearA = Integer.parseInt(partsA[0]);
            int monthA = Integer.parseInt(partsA[1]);
            
            String labelA = monthA + "/" + yearA;
            Map<Integer, BigDecimal> mapA = getMonthlyRevenue(yearA, monthA);
            
            String labelB = null;
            Map<Integer, BigDecimal> mapB = null;
            int daysB = 0;
            if (valueB != null && !valueB.trim().isEmpty()) {
                String[] partsB = parseMonthStr(valueB);
                int yearB = Integer.parseInt(partsB[0]);
                int monthB = Integer.parseInt(partsB[1]);
                labelB = monthB + "/" + yearB;
                mapB = getMonthlyRevenue(yearB, monthB);
                
                java.time.YearMonth ymB = java.time.YearMonth.of(yearB, monthB);
                daysB = ymB.lengthOfMonth();
            }
            
            java.time.YearMonth ymA = java.time.YearMonth.of(yearA, monthA);
            int daysA = ymA.lengthOfMonth();
            int maxDays = Math.max(daysA, daysB);
            
            for (int d = 1; d <= maxDays; d++) {
                if (d <= daysA) {
                    Map<String, Object> itemA = new HashMap<>();
                    itemA.put("label", "Ngày " + d);
                    itemA.put("value", mapA.getOrDefault(d, BigDecimal.ZERO));
                    itemA.put("type", "Doanh thu (" + labelA + ")");
                    list.add(itemA);
                }
                
                if (mapB != null && d <= daysB) {
                    Map<String, Object> itemB = new HashMap<>();
                    itemB.put("label", "Ngày " + d);
                    itemB.put("value", mapB.getOrDefault(d, BigDecimal.ZERO));
                    itemB.put("type", "So sánh (" + labelB + ")");
                    list.add(itemB);
                }
            }
        } else if ("day".equals(type)) {
            String dateA = parseDateStr(valueA);
            String dateB = (valueB != null && !valueB.trim().isEmpty()) ? parseDateStr(valueB) : null;
            
            String labelA = formatDateLabel(dateA);
            String labelB = dateB != null ? formatDateLabel(dateB) : null;
            
            Map<Integer, BigDecimal> mapA = getDailyRevenue(dateA);
            Map<Integer, BigDecimal> mapB = dateB != null ? getDailyRevenue(dateB) : null;
            
            for (int h = 0; h <= 23; h++) {
                Map<String, Object> itemA = new HashMap<>();
                itemA.put("label", h + "h");
                itemA.put("value", mapA.getOrDefault(h, BigDecimal.ZERO));
                itemA.put("type", "Doanh thu (" + labelA + ")");
                list.add(itemA);
                
                if (mapB != null) {
                    Map<String, Object> itemB = new HashMap<>();
                    itemB.put("label", h + "h");
                    itemB.put("value", mapB.getOrDefault(h, BigDecimal.ZERO));
                    itemB.put("type", "So sánh (" + labelB + ")");
                    list.add(itemB);
                }
            }
        }
        
        return list;
    }

    private Map<Integer, BigDecimal> getYearlyRevenue(int year) {
        String sql = "SELECT MONTH(ngay_tao) AS monthVal, SUM(tong_tien_thanh_toan) AS sumVal FROM hoa_don WHERE trang_thai = 4 AND YEAR(ngay_tao) = :year GROUP BY MONTH(ngay_tao)";
        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("year", year);
        List<Object[]> res = q.getResultList();
        Map<Integer, BigDecimal> map = new HashMap<>();
        for (Object[] row : res) {
            Integer month = ((Number) row[0]).intValue();
            BigDecimal sum = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            map.put(month, sum);
        }
        return map;
    }

    private Map<Integer, BigDecimal> getMonthlyRevenue(int year, int month) {
        String sql = "SELECT DAY(ngay_tao) AS dayVal, SUM(tong_tien_thanh_toan) AS sumVal FROM hoa_don WHERE trang_thai = 4 AND YEAR(ngay_tao) = :year AND MONTH(ngay_tao) = :month GROUP BY DAY(ngay_tao)";
        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("year", year);
        q.setParameter("month", month);
        List<Object[]> res = q.getResultList();
        Map<Integer, BigDecimal> map = new HashMap<>();
        for (Object[] row : res) {
            Integer day = ((Number) row[0]).intValue();
            BigDecimal sum = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            map.put(day, sum);
        }
        return map;
    }

    private Map<Integer, BigDecimal> getDailyRevenue(String date) {
        String sql = "SELECT DATEPART(hour, ngay_tao) AS hourVal, SUM(tong_tien_thanh_toan) AS sumVal FROM hoa_don WHERE trang_thai = 4 AND CAST(ngay_tao AS date) = :date GROUP BY DATEPART(hour, ngay_tao)";
        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("date", date);
        List<Object[]> res = q.getResultList();
        Map<Integer, BigDecimal> map = new HashMap<>();
        for (Object[] row : res) {
            Integer hour = ((Number) row[0]).intValue();
            BigDecimal sum = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            map.put(hour, sum);
        }
        return map;
    }

    private String[] parseMonthStr(String val) {
        if (val.contains("-")) {
            String[] parts = val.split("-");
            return new String[]{parts[0], parts[1]};
        } else if (val.contains("/")) {
            String[] parts = val.split("/");
            return new String[]{parts[1], parts[0]};
        }
        throw new IllegalArgumentException("Invalid month format: " + val);
    }

    private String parseDateStr(String val) {
        if (val.contains("-")) {
            return val;
        } else if (val.contains("/")) {
            String[] parts = val.split("/");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        }
        throw new IllegalArgumentException("Invalid date format: " + val);
    }

    private String formatDateLabel(String dateStr) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(dtf);
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, Object> getChiTietThongKe(LocalDateTime tuNgay, LocalDateTime denNgay, String tuGio, String denGio) {
        // Normalize tuGio and denGio
        if (tuGio == null || tuGio.trim().isEmpty()) {
            tuGio = "00:00:00";
        } else if (tuGio.length() == 5) {
            tuGio = tuGio + ":00";
        }
        if (denGio == null || denGio.trim().isEmpty()) {
            denGio = "23:59:59";
        } else if (denGio.length() == 5) {
            denGio = denGio + ":59";
        }

        Map<String, Object> result = new HashMap<>();

        // 1. Top selling products
        String sqlTopSell = "SELECT sp.ten_san_pham AS tenSanPham, " +
                            "       SUM(hdct.so_luong) AS soLuongDaBan, " +
                            "       SUM(hdct.so_luong * hdct.don_gia) AS doanhThu, " +
                            "       sp.hinh_anh AS hinhAnh, " +
                            "       (SELECT SUM(c.so_luong_ton) FROM chi_tiet_san_pham c WHERE c.id_san_pham = sp.id) AS ton " +
                            "FROM hoa_don_chi_tiet hdct " +
                            "JOIN hoa_don hd ON hdct.id_hoa_don = hd.id " +
                            "JOIN chi_tiet_san_pham ctsp ON hdct.id_chi_tiet_san_pham = ctsp.id " +
                            "JOIN san_pham sp ON ctsp.id_san_pham = sp.id " +
                            "WHERE hd.trang_thai = 4 AND hd.ngay_tao BETWEEN :tuNgay AND :denNgay " +
                            "  AND CAST(hd.ngay_tao AS time) BETWEEN :tuGio AND :denGio " +
                            "GROUP BY sp.id, sp.ten_san_pham, sp.hinh_anh " +
                            "ORDER BY SUM(hdct.so_luong) DESC";
        Query qTopSell = entityManager.createNativeQuery(sqlTopSell);
        qTopSell.setParameter("tuNgay", tuNgay);
        qTopSell.setParameter("denNgay", denNgay);
        qTopSell.setParameter("tuGio", tuGio);
        qTopSell.setParameter("denGio", denGio);
        qTopSell.setMaxResults(10);
        List<Object[]> resTopSell = qTopSell.getResultList();
        List<Map<String, Object>> topSellList = new ArrayList<>();
        for (Object[] row : resTopSell) {
            Map<String, Object> item = new HashMap<>();
            item.put("tenSanPham", row[0]);
            item.put("soLuongDaBan", row[1] != null ? ((Number) row[1]).longValue() : 0L);
            item.put("doanhThu", row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO);
            item.put("hinhAnh", row[3]);
            item.put("ton", row[4] != null ? ((Number) row[4]).longValue() : 0L);
            topSellList.add(item);
        }
        result.put("topBanChay", topSellList);

        // 2. Potential customers
        String sqlTopCustomers = "SELECT kh.ho_ten AS hoTen, kh.sdt AS sdt, COUNT(hd.id) AS soDonHang, SUM(hd.tong_tien_thanh_toan) AS tongChiTieu " +
                                 "FROM hoa_don hd " +
                                 "JOIN khach_hang kh ON hd.id_khach_hang = kh.id " +
                                 "WHERE hd.trang_thai = 4 AND hd.ngay_tao BETWEEN :tuNgay AND :denNgay " +
                                 "  AND CAST(hd.ngay_tao AS time) BETWEEN :tuGio AND :denGio " +
                                 "GROUP BY kh.id, kh.ho_ten, kh.sdt " +
                                 "ORDER BY COUNT(hd.id) DESC, SUM(hd.tong_tien_thanh_toan) DESC";
        Query qTopCust = entityManager.createNativeQuery(sqlTopCustomers);
        qTopCust.setParameter("tuNgay", tuNgay);
        qTopCust.setParameter("denNgay", denNgay);
        qTopCust.setParameter("tuGio", tuGio);
        qTopCust.setParameter("denGio", denGio);
        qTopCust.setMaxResults(10);
        List<Object[]> resTopCust = qTopCust.getResultList();
        List<Map<String, Object>> topCustList = new ArrayList<>();
        for (Object[] row : resTopCust) {
            Map<String, Object> item = new HashMap<>();
            item.put("hoTen", row[0]);
            item.put("sdt", row[1]);
            item.put("soDon", row[2] != null ? ((Number) row[2]).longValue() : 0L);
            item.put("tongChiTieu", row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO);
            topCustList.add(item);
        }
        result.put("topKhachHang", topCustList);

        // 3. Slow selling & inventory
        String sqlSlowSell = "SELECT sp.ten_san_pham AS tenSanPham, " +
                             "       COALESCE(SUM(sold.soLuong), 0) AS daBan, " +
                             "       SUM(ctsp.so_luong_ton) AS ton " +
                             "FROM chi_tiet_san_pham ctsp " +
                             "JOIN san_pham sp ON ctsp.id_san_pham = sp.id " +
                             "LEFT JOIN ( " +
                             "    SELECT hdct.id_chi_tiet_san_pham, SUM(hdct.so_luong) AS soLuong " +
                             "    FROM hoa_don_chi_tiet hdct " +
                             "    JOIN hoa_don hd ON hdct.id_hoa_don = hd.id " +
                             "    WHERE hd.trang_thai = 4 AND hd.ngay_tao BETWEEN :tuNgay AND :denNgay " +
                             "      AND CAST(hd.ngay_tao AS time) BETWEEN :tuGio AND :denGio " +
                             "    GROUP BY hdct.id_chi_tiet_san_pham " +
                             ") sold ON ctsp.id = sold.id_chi_tiet_san_pham " +
                             "GROUP BY sp.id, sp.ten_san_pham " +
                             "ORDER BY COALESCE(SUM(sold.soLuong), 0) ASC, SUM(ctsp.so_luong_ton) DESC";
        Query qSlowSell = entityManager.createNativeQuery(sqlSlowSell);
        qSlowSell.setParameter("tuNgay", tuNgay);
        qSlowSell.setParameter("denNgay", denNgay);
        qSlowSell.setParameter("tuGio", tuGio);
        qSlowSell.setParameter("denGio", denGio);
        qSlowSell.setMaxResults(10);
        List<Object[]> resSlowSell = qSlowSell.getResultList();
        List<Map<String, Object>> slowSellList = new ArrayList<>();
        for (Object[] row : resSlowSell) {
            Map<String, Object> item = new HashMap<>();
            item.put("tenSanPham", row[0]);
            item.put("daBan", row[1] != null ? ((Number) row[1]).longValue() : 0L);
            item.put("ton", row[2] != null ? ((Number) row[2]).longValue() : 0L);
            slowSellList.add(item);
        }
        result.put("banChamTonKho", slowSellList);

        // 4. Order statuses distribution
        String sqlStatusDist = "SELECT trang_thai, COUNT(id) FROM hoa_don WHERE ngay_tao BETWEEN :tuNgay AND :denNgay " +
                               "  AND CAST(ngay_tao AS time) BETWEEN :tuGio AND :denGio " +
                               "GROUP BY trang_thai";
        Query qStatusDist = entityManager.createNativeQuery(sqlStatusDist);
        qStatusDist.setParameter("tuNgay", tuNgay);
        qStatusDist.setParameter("denNgay", denNgay);
        qStatusDist.setParameter("tuGio", tuGio);
        qStatusDist.setParameter("denGio", denGio);
        List<Object[]> resStatusDist = qStatusDist.getResultList();
        Map<Integer, Long> statusMap = new HashMap<>();
        // Initialize default count for status 0 to 6
        for (int i = 0; i <= 6; i++) {
            statusMap.put(i, 0L);
        }
        for (Object[] row : resStatusDist) {
            if (row[0] != null) {
                Integer statusVal = ((Number) row[0]).intValue();
                Long countVal = ((Number) row[1]).longValue();
                statusMap.put(statusVal, countVal);
            }
        }
        result.put("trangThaiDonHang", statusMap);

        // 5. Products sold (details with size and color)
        String sqlProdSold = "SELECT sp.ten_san_pham AS tenSanPham, " +
                             "       ms.ten_mau_sac AS tenMauSac, " +
                             "       kc.ten_kich_thuoc AS tenKichCo, " +
                             "       SUM(hdct.so_luong) AS soLuongDaBan, " +
                             "       SUM(hdct.so_luong * hdct.don_gia) AS doanhThu, " +
                             "       MAX(ctsp.so_luong_ton) AS ton " +
                             "FROM hoa_don_chi_tiet hdct " +
                             "JOIN hoa_don hd ON hdct.id_hoa_don = hd.id " +
                             "JOIN chi_tiet_san_pham ctsp ON hdct.id_chi_tiet_san_pham = ctsp.id " +
                             "JOIN san_pham sp ON ctsp.id_san_pham = sp.id " +
                             "LEFT JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id " +
                             "LEFT JOIN kich_thuoc kc ON ctsp.id_kich_thuoc = kc.id " +
                             "WHERE hd.trang_thai = 4 " +
                             "  AND hd.ngay_tao BETWEEN :tuNgay AND :denNgay " +
                             "  AND CAST(hd.ngay_tao AS time) BETWEEN :tuGio AND :denGio " +
                             "GROUP BY sp.id, sp.ten_san_pham, ms.ten_mau_sac, kc.ten_kich_thuoc " +
                             "ORDER BY SUM(hdct.so_luong) DESC";
        Query qProdSold = entityManager.createNativeQuery(sqlProdSold);
        qProdSold.setParameter("tuNgay", tuNgay);
        qProdSold.setParameter("denNgay", denNgay);
        qProdSold.setParameter("tuGio", tuGio);
        qProdSold.setParameter("denGio", denGio);
        qProdSold.setMaxResults(50);
        List<Object[]> resProdSold = qProdSold.getResultList();
        List<Map<String, Object>> prodSoldList = new ArrayList<>();
        for (Object[] row : resProdSold) {
            Map<String, Object> item = new HashMap<>();
            item.put("tenSanPham", row[0]);
            item.put("tenMauSac", row[1]);
            item.put("tenKichCo", row[2]);
            item.put("soLuongDaBan", row[3] != null ? ((Number) row[3]).longValue() : 0L);
            item.put("doanhThu", row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO);
            item.put("ton", row[5] != null ? ((Number) row[5]).longValue() : 0L);
            prodSoldList.add(item);
        }
        result.put("sanPhamDaBan", prodSoldList);

        // 6. Payment methods stats
        String sqlPayments = "SELECT tt.id_hoa_don, tt.phuong_thuc, tt.so_tien, tt.ghi_chu " +
                             "FROM thanh_toan tt " +
                             "JOIN hoa_don hd ON tt.id_hoa_don = hd.id " +
                             "WHERE hd.trang_thai = 4 " +
                             "  AND hd.ngay_tao BETWEEN :tuNgay AND :denNgay " +
                             "  AND CAST(hd.ngay_tao AS time) BETWEEN :tuGio AND :denGio";
        Query qPayments = entityManager.createNativeQuery(sqlPayments);
        qPayments.setParameter("tuNgay", tuNgay);
        qPayments.setParameter("denNgay", denNgay);
        qPayments.setParameter("tuGio", tuGio);
        qPayments.setParameter("denGio", denGio);
        List<Object[]> resPayments = qPayments.getResultList();

        String sqlInvoices = "SELECT hd.id, COALESCE(hd.tong_tien_thanh_toan, 0) " +
                             "FROM hoa_don hd " +
                             "WHERE hd.trang_thai = 4 " +
                             "  AND hd.ngay_tao BETWEEN :tuNgay AND :denNgay " +
                             "  AND CAST(hd.ngay_tao AS time) BETWEEN :tuGio AND :denGio";
        Query qInvoices = entityManager.createNativeQuery(sqlInvoices);
        qInvoices.setParameter("tuNgay", tuNgay);
        qInvoices.setParameter("denNgay", denNgay);
        qInvoices.setParameter("tuGio", tuGio);
        qInvoices.setParameter("denGio", denGio);
        List<Object[]> resInvoices = qInvoices.getResultList();

        BigDecimal tm = BigDecimal.ZERO;
        BigDecimal ck = BigDecimal.ZERO;
        BigDecimal vn = BigDecimal.ZERO;

        Set<Long> invoicesWithPaymentRecord = new HashSet<>();

        for (Object[] row : resPayments) {
            if (row == null) continue;
            Long hdId = ((Number) row[0]).longValue();
            invoicesWithPaymentRecord.add(hdId);

            String phuongThuc = row[1] != null ? row[1].toString().trim() : "";
            BigDecimal soTien = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            String ghiChu = row[3] != null ? row[3].toString() : "";

            if ("1".equals(phuongThuc) || "Tiền mặt".equalsIgnoreCase(phuongThuc)) {
                tm = tm.add(soTien);
            } else if ("2".equals(phuongThuc) || "Chuyển khoản".equalsIgnoreCase(phuongThuc)) {
                if (ghiChu != null && ghiChu.toUpperCase().contains("VNPAY")) {
                    vn = vn.add(soTien);
                } else {
                    ck = ck.add(soTien);
                }
            } else if (phuongThuc.toUpperCase().contains("VNPAY")) {
                vn = vn.add(soTien);
            } else {
                tm = tm.add(soTien);
            }
        }

        // Add COD for completed invoices that have no payment records (defaulting to Cash/COD)
        for (Object[] row : resInvoices) {
            if (row == null) continue;
            Long hdId = ((Number) row[0]).longValue();
            BigDecimal tongTien = new BigDecimal(row[1].toString());
            if (!invoicesWithPaymentRecord.contains(hdId)) {
                tm = tm.add(tongTien);
            }
        }

        Map<String, Object> paymentStatsMap = new HashMap<>();
        paymentStatsMap.put("tienMat", tm);
        paymentStatsMap.put("chuyenKhoan", ck);
        paymentStatsMap.put("vnpay", vn);
        paymentStatsMap.put("tongTien", tm.add(ck).add(vn));

        result.put("thongKeTien", paymentStatsMap);

        return result;
    }
}
