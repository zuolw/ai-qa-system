package com.ai.qa.service.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}