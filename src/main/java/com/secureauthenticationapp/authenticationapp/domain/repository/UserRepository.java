package com.secureauthenticationapp.authenticationapp.domain.repository;

import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String lowerCase);

    Optional<UserEntity> findByEmail(String lowerCase);
}
