package com.ai.qa.user.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_history")
@Data
public class QAHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private String answer;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}
