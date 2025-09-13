package com.ai.qa.service.infrastructure.persistence.repositories;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO> {

    QAHistoryPO findHistoryById(String userId);

}
