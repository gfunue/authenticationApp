package com.secureauthenticationapp.authenticationapp.domain.repository;

import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<BlogEntity, Long> {
}
