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

    private Object rag;

    public String getId(){
        return this.id;
    }

    /**
     *
     * @param question
     * @return
     */
    public String getAnswer(String question) {
        String response = rag.getContext();
        return answer+response;
    }

    private QAHistory(String id){

    }

    public String getUserId(){

    }

    public String getRAGAnswer(){

        getAnswer();
        serivice.sss();
        return  "";
    }
    public static QAHistory createNew(String userId, String question, String answer,...){


        return new QAHistory();
    }
}
