package com.ai.qa.service.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class QAHistory {

    private String id;
    private String userId;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private String sessionId;

    private QARAG rag;

    public String getId() {
        return this.id;
    }

    /**
     *
     * @param question
     * @return
     */
    public String getAnswer(String question) {
        String response = rag.getContext();
        return answer + response;
    }

    private QAHistory(String id) {

    }

    // Constructor for persistence layer
    public QAHistory(String id, String userId, String question, String answer, LocalDateTime timestamp,
            String sessionId) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getRAGAnswer() {
        // 修复：调用getAnswer方法时需要传入question参数
        String baseAnswer = getAnswer(question);
        // 修复：移除未定义的serivice调用
        return baseAnswer;
    }

    public static QAHistory createNew(String userId, String question, String answer, String sessionId) {
        QAHistory qaHistory = new QAHistory(java.util.UUID.randomUUID().toString());
        qaHistory.userId = userId;
        qaHistory.question = question;
        qaHistory.answer = answer;
        qaHistory.sessionId = sessionId;
        qaHistory.timestamp = LocalDateTime.now();
        return qaHistory;
    }
}
