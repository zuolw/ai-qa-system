package com.ai.qa.service.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QAHistoryDTO {
    private String id;
    private String userId;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private String sessionId;
}
