package com.secureauthenticationapp.authenticationapp.domain.repository;

import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogEntity, Long> {

    @Query("SELECT b FROM BlogEntity b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.intro) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.conclusion) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BlogEntity> searchByKeyword(String keyword);
}
