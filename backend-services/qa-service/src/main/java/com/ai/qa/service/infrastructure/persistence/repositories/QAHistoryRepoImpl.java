package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    private final Mapper mapper = new Mapper();

    @Override
    public void save(QAHistory history) {
        QAHistoryPO qaHistoryPO = mapper.toPO(history);
        jpaQAHistoryRepository.save(qaHistoryPO);
    }

    @Override
    public Optional<QAHistory> findHistoryById(String id) {
        QAHistoryPO qaHistoryPO = jpaQAHistoryRepository.findById(id).orElse(null);
        return Optional.ofNullable(mapper.toDomain(qaHistoryPO));
    }

    @Override
    public List<QAHistory> findHistoryBySession(String sessionId) {
        List<QAHistoryPO> poList = jpaQAHistoryRepository.findBySessionId(sessionId);
        return poList.stream().map(mapper::toDomain).toList();
    }
}
