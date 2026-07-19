package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.Dto.Request.ChatbotRequest;
import com.example.be_dantn.Dto.Response.SanPhamResponse;
import com.example.be_dantn.Entity.PhieuGiamGia;
import com.example.be_dantn.Entity.ThuongHieu;
import com.example.be_dantn.Entity.ChatLieu;
import com.example.be_dantn.Repository.PhieuGiamGiaRepository;
import com.example.be_dantn.Repository.SanPhamRepository;
import com.example.be_dantn.Repository.ThuongHieuRepository;
import com.example.be_dantn.Repository.ChatLieuRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final SanPhamRepository sanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
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

            // 2. Fetch data from DB for RAG with strict LIMITS (RAG Optimization)
            List<SanPhamResponse> products = sanPhamRepository.findSanPhamByFilters(
                    keyword, idThuongHieu, idChatLieu, 1, PageRequest.of(0, 10)
            ).getContent();

            List<PhieuGiamGia> vouchers = phieuGiamGiaRepository.findByFilters(
                    null, null, null, null, 1, PageRequest.of(0, 5)
            ).getContent();

            // 3. Format database results
            StringBuilder productsText = new StringBuilder();
            if (products.isEmpty()) {
                // If no matching keyword products, fetch top 10 generic active products
                products = sanPhamRepository.findSanPhamByFilters(
                        null, null, null, 1, PageRequest.of(0, 10)
                ).getContent();
            }
            
            for (SanPhamResponse sp : products) {
                productsText.append(String.format("- ID: %d, Tên: %s, Mã: %s, Thương hiệu: %s, Chất liệu: %s, Giá: %sđ, Tồn kho: %d\n",
                        sp.getId(),
                        sp.getTenSanPham(),
                        sp.getMaSanPham(),
                        sp.getTenThuongHieu(),
                        sp.getTenChatLieu(),
                        sp.getGiaThapNhatSauGiam() != null ? sp.getGiaThapNhatSauGiam().setScale(0).toString() : "Liên hệ",
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

            // 4. Define system instructions (with realtime timestamp for data freshness)
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String systemInstructionText = "Bạn là Trợ lý AI (tên: BeeBot) của cửa hàng thời trang Bee Stylish.\n" +
                    "Cửa hàng bán các sản phẩm thời trang phong cách tối giản, thanh lịch, chất lượng.\n" +
                    "Dưới đây là thông tin cửa hàng:\n" +
                    "- Địa chỉ:  Trịnh Văn Bô,Nam Từ Liêm,Cầu Giấy, TP. Hà Nội\n" +
                    "- Hotline: 1900 123 456\n" +
                    "- Email: hello@beestylish.com\n\n" +
                    "⚠️ DỮ LIỆU REALTIME (Cập nhật lúc " + timestamp + "):\n" +
                    "QUAN TRỌNG: Dữ liệu sản phẩm và voucher bên dưới là DỮ LIỆU MỚI NHẤT, được truy vấn trực tiếp từ cơ sở dữ liệu tại thời điểm hiện tại. " +
                    "Nếu thông tin sản phẩm (giá, tồn kho, tên, thuộc tính) trong lịch sử hội thoại trước đó khác với dữ liệu bên dưới, hãy LUÔN LUÔN ưu tiên sử dụng dữ liệu bên dưới vì nó chính xác hơn. " +
                    "Dữ liệu cũ trong lịch sử chat có thể đã lỗi thời do admin đã cập nhật sản phẩm.\n\n" +
                    "Danh sách sản phẩm hiện có tại cửa hàng (giá đã giảm nếu có):\n" + productsText + "\n" +
                    "Danh sách vouchers khuyến mãi hiện có:\n" + vouchersText + "\n\n" +
                    "Hướng dẫn trả lời:\n" +
                    "1. Trả lời bằng tiếng Việt, lịch sự, thân thiện. Xưng hô 'em/dạ' và 'anh/chị/bạn'.\n" +
                    "2. Khi giới thiệu sản phẩm, hãy chèn link sản phẩm theo định dạng chuẩn Markdown: `[Tên sản phẩm](/product/ID)`. Ví dụ: `[Áo Polo Nam](/product/12)`. Rất quan trọng để khách hàng nhấp chuột truy cập trực tiếp.\n" +
                    "3. Trình bày ngắn gọn bằng bullet points, sử dụng icon/emoji sinh động để giao diện chat trực quan hơn.\n" +
                    "4. Nếu khách hàng muốn gặp nhân viên hỗ trợ, hãy khuyên họ bấm nút 'Gặp nhân viên hỗ trợ' ngay phía trên ô chat để em kết nối trực tiếp.\n" +
                    "5. Khi trả lời câu hỏi về giá, sắp xếp, so sánh sản phẩm: LUÔN dựa trên dữ liệu sản phẩm ở phần '⚠️ DỮ LIỆU REALTIME' phía trên, KHÔNG dựa trên các câu trả lời trước trong lịch sử hội thoại.";

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

            // Append current message
            Map<String, Object> currentMessageContent = new HashMap<>();
            currentMessageContent.put("role", "user");
            List<Map<String, String>> currentParts = new ArrayList<>();
            Map<String, String> currentPart = new HashMap<>();
            currentPart.put("text", message);
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
