package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, String> {

    List<QAHistoryPO> findByUserId(String userId);

    List<QAHistoryPO> findBySessionId(String sessionId);

}
