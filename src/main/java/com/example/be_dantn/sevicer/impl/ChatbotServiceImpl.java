package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.ChatbotRequest;
import com.example.be_dantn.Dto.Response.ChiTietSanPhamResponseDTO;
import com.example.be_dantn.Dto.Response.SanPhamResponse;
import com.example.be_dantn.Entity.PhieuGiamGia;
import com.example.be_dantn.Entity.ThuongHieu;
import com.example.be_dantn.Entity.ChatLieu;
import com.example.be_dantn.Repository.ChiTietSanPhamRepository;
import com.example.be_dantn.Repository.PhieuGiamGiaRepository;
import com.example.be_dantn.Repository.SanPhamRepository;
import com.example.be_dantn.Repository.ThuongHieuRepository;
import com.example.be_dantn.Repository.ChatLieuRepository;
import com.example.be_dantn.repository.HoaDonChiTietRepository;
import com.example.be_dantn.sevicer.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final RestTemplate restTemplate;

    private static final String GEMINI_API_KEY = "AIzaSyBBsCIXuv_14IlRiEpWqKRefy4Xdrfwj7s";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;

    @Override
    public String getResponse(ChatbotRequest request) {
        try {
            String message = request.getMessage() != null ? request.getMessage() : "";
            String lowerMsg = message.toLowerCase();

            // 1. Keyword extraction for RAG optimization
            String keyword = null;
            if (lowerMsg.contains("polo")) keyword = "Polo";
            else if (lowerMsg.contains("sơ mi")) keyword = "Sơ mi";
            else if (lowerMsg.contains("thun")) keyword = "Thun";
            else if (lowerMsg.contains("áo")) keyword = "Áo";
            else if (lowerMsg.contains("quần")) keyword = "Quần";
            else if (lowerMsg.contains("khoác")) keyword = "Khoác";

            // Brand matching
            Long idThuongHieu = null;
            List<ThuongHieu> brands = thuongHieuRepository.findAll();
            for (ThuongHieu th : brands) {
                if (th.getTenThuongHieu() != null && lowerMsg.contains(th.getTenThuongHieu().toLowerCase())) {
                    idThuongHieu = th.getId();
                    break;
                }
            }

            // Material matching
            Long idChatLieu = null;
            List<ChatLieu> materials = chatLieuRepository.findAll();
            for (ChatLieu cl : materials) {
                if (cl.getTenChatLieu() != null && lowerMsg.contains(cl.getTenChatLieu().toLowerCase())) {
                    idChatLieu = cl.getId();
                    break;
                }
            }

            // 2. Fetch data from DB for RAG with up to 100 products for complete catalog coverage
            List<SanPhamResponse> products = sanPhamRepository.findSanPhamByFilters(
                    keyword, idThuongHieu, idChatLieu, 1, PageRequest.of(0, 100)
            ).getContent();

            List<PhieuGiamGia> vouchers = phieuGiamGiaRepository.findByFilters(
                    null, null, null, null, 1, PageRequest.of(0, 20)
            ).getContent();

            // Fetch top-selling products based on completed orders
            List<Object[]> topSellingRows = hoaDonChiTietRepository.findTopSellingProducts();
            StringBuilder topSellingText = new StringBuilder();
            if (topSellingRows != null && !topSellingRows.isEmpty()) {
                int rank = 1;
                for (Object[] row : topSellingRows) {
                    Long spId = ((Number) row[0]).longValue();
                    String spTen = row[1] != null ? row[1].toString() : "";
                    Long daBan = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                    topSellingText.append(String.format("- Top %d: [ID: %d] %s (Đã bán: %d sản phẩm)\n", rank++, spId, spTen, daBan));
                }
            } else {
                topSellingText.append("- Tất cả các sản phẩm của shop đều đang bán rất tốt và sẵn hàng.\n");
            }

            // If keyword yielded empty results, fetch all active products (up to 100)
            if (products.isEmpty()) {
                products = sanPhamRepository.findSanPhamByFilters(
                        null, null, null, 1, PageRequest.of(0, 100)
                ).getContent();
            }

            // Query all active variants to aggregate available colors and sizes per product
            List<ChiTietSanPhamResponseDTO> activeVariants = chiTietSanPhamRepository.findByFiltersForExcel(
                    null, null, null, 1, null, null, null
            );

            Map<String, Set<String>> productColorsMap = new HashMap<>();
            Map<String, Set<String>> productSizesMap = new HashMap<>();

            if (activeVariants != null) {
                for (ChiTietSanPhamResponseDTO var : activeVariants) {
                    if (var.getMaSanPham() != null) {
                        if (var.getTenMauSac() != null && !var.getTenMauSac().isBlank()) {
                            productColorsMap.computeIfAbsent(var.getMaSanPham(), k -> new LinkedHashSet<>()).add(var.getTenMauSac());
                        }
                        if (var.getTenKichCo() != null && !var.getTenKichCo().isBlank()) {
                            productSizesMap.computeIfAbsent(var.getMaSanPham(), k -> new LinkedHashSet<>()).add(var.getTenKichCo());
                        }
                    }
                }
            }

            // 3. Format database results with full real-time product & variant attributes
            StringBuilder productsText = new StringBuilder();
            for (SanPhamResponse sp : products) {
                String giaDisplay = "Liên hệ";
                if (sp.getGiaThapNhatSauGiam() != null) {
                    if (sp.getGiaCaoNhatSauGiam() != null && sp.getGiaThapNhatSauGiam().compareTo(sp.getGiaCaoNhatSauGiam()) != 0) {
                        giaDisplay = sp.getGiaThapNhatSauGiam().setScale(0).toString() + "đ ~ " + sp.getGiaCaoNhatSauGiam().setScale(0).toString() + "đ";
                    } else {
                        giaDisplay = sp.getGiaThapNhatSauGiam().setScale(0).toString() + "đ";
                    }
                }

                Set<String> colors = productColorsMap.getOrDefault(sp.getMaSanPham(), Collections.emptySet());
                Set<String> sizes = productSizesMap.getOrDefault(sp.getMaSanPham(), Collections.emptySet());
                String colorsStr = colors.isEmpty() ? "Đa dạng" : String.join(", ", colors);
                String sizesStr = sizes.isEmpty() ? "Đa dạng" : String.join(", ", sizes);

                productsText.append(String.format("- ID: %d, Tên: %s (Mã: %s), Thương hiệu: %s, Chất liệu: %s, Màu sắc hiện có: [%s], Kích thước hiện có: [%s], Giá bán thực tế hiện tại: %s, Tổng tồn kho: %d\n",
                        sp.getId(),
                        sp.getTenSanPham(),
                        sp.getMaSanPham(),
                        sp.getTenThuongHieu() != null ? sp.getTenThuongHieu() : "Khác",
                        sp.getTenChatLieu() != null ? sp.getTenChatLieu() : "Khác",
                        colorsStr,
                        sizesStr,
                        giaDisplay,
                        sp.getTongTonKho()
                ));
            }

            StringBuilder vouchersText = new StringBuilder();
            for (PhieuGiamGia v : vouchers) {
                String unit = (v.getLoaiGiam() != null && v.getLoaiGiam() == 0) ? "%" : "đ";
                vouchersText.append(String.format("- Code: %s, Tên: %s, Giảm: %s%s, Đơn tối thiểu: %sđ, Hạn: %s\n",
                        v.getMaPhieuGiamGia(),
                        v.getTenPhieuGiamGia(),
                        v.getGiaTri() != null ? v.getGiaTri().setScale(0).toString() : "0",
                        unit,
                        v.getDieuKienGiam() != null ? v.getDieuKienGiam().setScale(0).toString() : "0",
                        v.getNgayKetThuc() != null ? v.getNgayKetThuc().toString() : "không giới hạn"
                ));
            }

            // 4. Define system instructions with comprehensive real-time attribute override rules & natural customer service tone
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String systemInstructionText = "Bạn là Trợ lý hỗ trợ khách hàng (tên: BeeBot) của cửa hàng thời trang Bee Stylish.\n" +
                    "Cửa hàng bán các sản phẩm thời trang phong cách tối giản, thanh lịch, chất lượng.\n" +
                    "Dưới đây là thông tin cửa hàng:\n" +
                    "- Địa chỉ: Trịnh Văn Bô, Nam Từ Liêm, Hà Nội\n" +
                    "- Hotline: 1900 123 456\n" +
                    "- Email: hello@beestylish.com\n\n" +
                    "🔴 QUY TẮC BẮT BUỘC DÀNH CHO NỘI BỘ AI (Cập nhật lúc " + timestamp + "):\n" +
                    "1. Tất cả thông tin thuộc tính bên dưới (Tên sản phẩm, Mã sản phẩm, Giá bán, Chất liệu, Thương hiệu, Màu sắc, Kích thước, Tồn kho, Khuyến mãi, Mã giảm giá, Top bán chạy) được TRUY VẤN NGUYÊN BẢN TỪ DATABASE MỚI NHẤT ngay tại thời điểm tin nhắn này được gửi đi.\n" +
                    "2. TẤT CẢ THUỘC TÍNH SẢN PHẨM HOẶC VOUCHER ĐÃ TỪNG XUẤT HIỆN TRONG LỊCH SỬ CHAT TRƯỚC ĐÓ CÓ THỂ ĐÃ BỊ THAY ĐỔI / CẬP NHẬT TRONG CƠ SỞ DỮ LIỆU do Admin vừa thao tác.\n" +
                    "3. Khi người dùng hỏi hoặc yêu cầu so sánh/tìm kiếm thông tin (về bán chạy nhất, giá cả, màu sắc, chất liệu, thương hiệu, sản phẩm đắt nhất/rẻ nhất, voucher, tồn kho, v.v.):\n" +
                    "   👉 BẠN BẮT BUỘC PHẢI LUÔN LUÔN ĐỌC VÀ LẤY THÔNG TIN TỪ 'DANH SÁCH SẢN PHẨM BÁN CHẠY NHẤT' VÀ 'Danh sách sản phẩm hiện có' BÊN DƯỚI.\n" +
                    "   👉 BẠN PHẢI HOÀN TOÀN BỎ QUA CÁC CÂU TRẢ LỜI CŨ CỦA CHÍNH BẠN TRONG LỊCH SỬ CHAT NẾU CÓ BẤT KỲ SỰ KHÁC BIỆT NÀO SO VỚI DỮ LIỆU BÊN DƯỚI.\n\n" +
                    "🚫 QUY ĐỊNH VỀ TỪ NGỮ VÀ PHONG CÁCH GIAO TIẾP VỚI KHÁCH HÀNG (CỰC KỲ QUAN TRỌNG):\n" +
                    "1. TUYỆT ĐỐI KHÔNG ĐƯỢC NÓI các từ ngữ kỹ thuật công nghệ như: 'REAL-TIME', 'Realtime', 'Database', 'CSDL', 'Hệ thống AI', 'Thuật toán', 'RAG', 'Prompt', 'Dữ liệu backend' với khách hàng!\n" +
                    "2. TUYỆT ĐỐI KHÔNG NÓI 'hệ thống chưa có chức năng thống kê'! Hãy luôn tư vấn nhiệt tình dựa vào danh sách sản phẩm và top bán chạy được cung cấp bên dưới.\n" +
                    "3. Hãy xưng 'em' và gọi 'anh/chị/bạn', nói chuyện tự nhiên như một tư vấn viên bán hàng thực thụ của shop. Nếu cần khẳng định lại thông tin, hãy dùng cách nói tự nhiên như: 'Dạ em vừa kiểm tra lại trên hệ thống shop...', 'Theo cập nhật mới nhất tại cửa hàng...', 'Dạ thông tin chính xác hiện tại là...'.\n" +
                    "4. Khi giới thiệu sản phẩm, hãy chèn link sản phẩm theo định dạng chuẩn Markdown: `[Tên sản phẩm](/product/ID)`. Ví dụ: `[Áo Polo Nam](/product/12)`.\n" +
                    "5. Trình bày ngắn gọn bằng bullet points, sử dụng icon/emoji sinh động.\n" +
                    "6. Nếu khách hàng muốn gặp nhân viên hỗ trợ, hãy khuyên họ bấm nút 'Gặp nhân viên hỗ trợ' ngay phía trên ô chat để em kết nối trực tiếp.\n\n" +
                    "DANH SÁCH SẢN PHẨM BÁN CHẠY NHẤT HỆ THỐNG (TOP SELLING):\n" + topSellingText + "\n" +
                    "Danh sách sản phẩm hiện có tại cửa hàng (Thông tin thuộc tính thực tế mới nhất từ Database):\n" + productsText + "\n" +
                    "Danh sách vouchers khuyến mãi hiện có:\n" + vouchersText + "\n";

            // 5. Construct request payload for Gemini API
            Map<String, Object> requestBody = new HashMap<>();

            // Build contents
            List<Map<String, Object>> contents = new ArrayList<>();

            // History mapping
            if (request.getHistory() != null) {
                for (ChatbotRequest.Content historyContent : request.getHistory()) {
                    Map<String, Object> c = new HashMap<>();
                    c.put("role", historyContent.getRole());
                    
                    List<Map<String, String>> parts = new ArrayList<>();
                    if (historyContent.getParts() != null) {
                        for (ChatbotRequest.Part part : historyContent.getParts()) {
                            Map<String, String> p = new HashMap<>();
                            p.put("text", part.getText());
                            parts.add(p);
                        }
                    }
                    c.put("parts", parts);
                    contents.add(c);
                }
            }

            // Append current message with fresh real-time DB payload directly in prompt turn
            Map<String, Object> currentMessageContent = new HashMap<>();
            currentMessageContent.put("role", "user");
            List<Map<String, String>> currentParts = new ArrayList<>();
            Map<String, String> currentPart = new HashMap<>();

            String promptWithRealtimeContext = String.format("""
                    [THÔNG TIN DỮ LIỆU THỰC TẾ TRUY VẤN TỪ HỆ THỐNG MỚI NHẤT DÀNH CHO LƯỢT HỎI NÀY (Lấy lúc %s)]:
                    Vui lòng tính toán và trả lời câu hỏi bên dưới DỰA HOÀN TOÀN VÀO DỮ LIỆU SẢN PHẨM, VOUCHER VÀ TOP BÁN CHẠY MỚI NHẤT NÀY.
                    Nếu trong lịch sử hội thoại trước đó bạn đã trả lời bất kỳ thông tin nào khác mà khác với bảng dữ liệu dưới đây, BẠN BẮT BUỘC PHẢI BỎ QUA CÂU TRẢ LỜI CŨ TRONG LỊCH SỬ CHAT, và chỉ trả lời đúng theo dữ liệu mới nhất này.

                    DANH SÁCH SẢN PHẨM BÁN CHẠY NHẤT HỆ THỐNG (TOP SELLING):
                    %s
                    DANH SÁCH SẢN PHẨM MỚI NHẤT TẠI CỬA HÀNG:
                    %s
                    DANH SÁCH VOUCHERS MỚI NHẤT:
                    %s

                    CÂU HỎI CỦA KHÁCH HÀNG: %s
                    """, timestamp, topSellingText.toString(), productsText.toString(), vouchersText.toString(), message);

            currentPart.put("text", promptWithRealtimeContext);
            currentParts.add(currentPart);
            currentMessageContent.put("parts", currentParts);
            contents.add(currentMessageContent);

            requestBody.put("contents", contents);

            // Build systemInstruction
            Map<String, Object> systemInstruction = new HashMap<>();
            List<Map<String, String>> instructionParts = new ArrayList<>();
            Map<String, String> instructionPart = new HashMap<>();
            instructionPart.put("text", systemInstructionText);
            instructionParts.add(instructionPart);
            systemInstruction.put("parts", instructionParts);
            requestBody.put("systemInstruction", systemInstruction);

            // Send to Gemini via RestTemplate
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_API_URL, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map body = response.getBody();
                List candidates = (List) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    if (content != null) {
                        List parts = (List) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            Map firstPart = (Map) parts.get(0);
                            return (String) firstPart.get("text");
                        }
                    }
                }
            }
            return "Dạ, hệ thống đang gặp lỗi kết nối với trợ lý AI. Anh/chị vui lòng thử lại sau ạ!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Dạ, hệ thống đang gặp lỗi kết nối với trợ lý AI. Anh/chị vui lòng thử lại sau ạ!";
        }
    }
}
