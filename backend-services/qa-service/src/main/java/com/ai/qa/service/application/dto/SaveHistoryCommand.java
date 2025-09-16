package com.ai.qa.service.application.dto;

import lombok.Data;

@Data
public class SaveHistoryCommand {
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
}
