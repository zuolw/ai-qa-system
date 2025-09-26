package com.ai.qa.user.application;

import com.ai.qa.user.api.dto.QAHistoryDTO;
import com.ai.qa.user.infrastructure.persistence.entities.QAHistory;
import com.ai.qa.user.infrastructure.persistence.repositories.QAHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QAHistoryService {

    private final QAHistoryRepository qaHistoryRepository;

    public List<QAHistoryDTO> getQAHistoryByUserId(Long userId) {
        List<QAHistory> histories = qaHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return histories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private QAHistoryDTO convertToDTO(QAHistory entity) {
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setQuestion(entity.getQuestion());
        dto.setAnswer(entity.getAnswer());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }
}
