package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Request.ChatbotRequest;
import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.ChatbotService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    
    // In-memory rate limiting map: IP -> request timestamps
    private final Map<String, List<Long>> ipRequestTimestamps = new ConcurrentHashMap<>();

    @PostMapping("/chat")
    public ResponseEntity<ResponseObject<String>> chat(
            @RequestBody ChatbotRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String clientIp = httpServletRequest.getRemoteAddr();
        
        // Rate Limiter: max 10 requests per minute
        if (isRateLimited(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ResponseObject<>(
                            HttpStatus.TOO_MANY_REQUESTS,
                            "Bạn đang gửi tin nhắn quá nhanh. Vui lòng thử lại sau ít phút!",
                            null
                    ));
        }

        String response = chatbotService.getResponse(request);
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Thành công", response));
    }

    private boolean isRateLimited(String ip) {
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - 60000;
        
        List<Long> timestamps = ipRequestTimestamps.computeIfAbsent(ip, k -> new CopyOnWriteArrayList<>());
        
        // Remove timestamps older than 1 minute
        timestamps.removeIf(t -> t < oneMinuteAgo);
        
        if (timestamps.size() >= 10) {
            return true;
        }
        
        timestamps.add(now);
        return false;
    }
}
