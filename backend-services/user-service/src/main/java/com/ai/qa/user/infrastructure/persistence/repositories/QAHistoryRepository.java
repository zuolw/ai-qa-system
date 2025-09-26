package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.infrastructure.persistence.entities.QAHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QAHistoryRepository extends JpaRepository<QAHistory, Long> {

    List<QAHistory> findByUserIdOrderByCreateTimeDesc(Long userId);
}
