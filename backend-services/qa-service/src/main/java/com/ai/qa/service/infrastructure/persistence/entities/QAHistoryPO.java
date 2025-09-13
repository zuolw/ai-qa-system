package com.ai.qa.service.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name= "qa_history")
public class QAHistoryPO {

    private String id;
    private String userId;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private String sessionId;

    private LocalDateTime create_time;
    private LocalDateTime update_time;

}
