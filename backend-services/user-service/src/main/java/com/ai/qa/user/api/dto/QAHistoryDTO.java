package com.ai.qa.user.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QAHistoryDTO {

    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private LocalDateTime createTime;
}
