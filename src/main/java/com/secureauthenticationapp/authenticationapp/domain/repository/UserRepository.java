package com.secureauthenticationapp.authenticationapp.domain.repository;

import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
