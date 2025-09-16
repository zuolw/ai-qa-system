package com.ai.qa.user.infrastructure.persistence.repositories;

import com.ai.qa.user.infrastructure.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
