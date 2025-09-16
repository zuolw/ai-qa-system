package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

public class Mapper {

    public QAHistoryPO toPO(QAHistory history) {
        QAHistoryPO po = new QAHistoryPO();
        po.setId(history.getId());
        po.setUserId(history.getUserId());
        po.setQuestion(history.getQuestion());
        po.setAnswer(history.getAnswer());
        po.setTimestamp(history.getTimestamp());
        po.setSessionId(history.getSessionId());
        // Assuming createTime and updateTime are set elsewhere or not needed
        return po;
    }

    public QAHistory toDomain(QAHistoryPO po) {
        if (po == null)
            return null;
        return new QAHistory(po.getId(), po.getUserId(), po.getQuestion(), po.getAnswer(), po.getTimestamp(),
                po.getSessionId());
    }
}
