package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    private Mapper mapper;

    @Override
    public void save(QAHistory history) {
        QAHistoryPO qaHistoryPO = mapper.toPO(history);
        jpaQAHistoryRepository.save(jpaQAHistoryRepository);
    }

    @Override
    public Optional<QAHistory> findHistoryById(String id) {
        //ddd
        QAHistoryPO qaHistoryPO = jpaQAHistoryRepository.findHistoryById(id);
        return mapper.toDomain(qaHistoryPO);
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        //ddd
        return null;
    }
}
