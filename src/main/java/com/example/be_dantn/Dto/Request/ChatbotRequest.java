package com.example.be_dantn.Dto.Request;

import lombok.Data;
import java.util.List;

@Data
public class ChatbotRequest {
    private String message;
    private List<Content> history;

    @Data
    public static class Content {
        private String role; // "user" or "model"
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }
}
