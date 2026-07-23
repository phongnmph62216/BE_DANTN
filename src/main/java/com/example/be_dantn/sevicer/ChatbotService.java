package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.Request.ChatbotRequest;

public interface ChatbotService {
    String getResponse(ChatbotRequest request);
}
