package com.ai.qa.service.application.service;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.domain.model.QAHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QAHistoryService {

    // 这里应该注入QAHistoryRepo，但暂时注释掉以避免编译错误
    // private final QAHistoryRepo qaHistoryRepo;

    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        // 修复：createNew需要4个参数
        QAHistory history = QAHistory.createNew(
                command.getUserId(),
                command.getQuestion(),
                command.getAnswer(),
                command.getSessionId() != null ? command.getSessionId() : "default");

        // 暂时注释掉repo调用
        // qaHistoryRepo.save(history);

        return toDto(history);
    }

    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query) {
        // 暂时返回空列表
        // List<QAHistory> historyList =
        // qaHistoryRepo.findHistoryByUserId(query.getUserId());

        return List.of(); // 暂时返回空列表
    }

    private QAHistoryDTO toDto(QAHistory history) {
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setQuestion(history.getQuestion());
        dto.setAnswer(history.getAnswer());
        dto.setTimestamp(history.getTimestamp());
        dto.setSessionId(history.getSessionId());
        return dto;
    }

    private List<QAHistoryDTO> toDtoList(List<QAHistory> historyList) {
        return historyList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
