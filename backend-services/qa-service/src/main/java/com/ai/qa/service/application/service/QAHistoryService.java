package com.ai.qa.service.application.service;

import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.domain.model.QAHistory;

public class QAHistoryService {

    public QAHistoryDto saveHistory(SaveHistoryCommand command){
        //command.getUserid!=null
        QAHistory history = QAHistory.createNew("","","");
        repo.save(history);
        return toDto(history);
    }

    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query){
//        query.getUserId

        List<QAHistory> historyList=  qaHistory.findHistoryByUserId(query.getUserId);

        return toDtoList(historyList);
    }
}
