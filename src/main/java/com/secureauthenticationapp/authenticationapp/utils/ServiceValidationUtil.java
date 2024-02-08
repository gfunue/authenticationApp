package com.secureauthenticationapp.authenticationapp.utils;

import com.secureauthenticationapp.authenticationapp.domain.exception.EmailAlreadyExistException;
import com.secureauthenticationapp.authenticationapp.domain.exception.UsernameAlreadyTakenException;
import com.secureauthenticationapp.authenticationapp.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ServiceValidationUtil {

    private final UserRepository userRepository;

    public void checkIfUserNameExists(String userName) {
        if (userRepository.findByUsername(userName.trim().toLowerCase()).isPresent()) {
            throw new UsernameAlreadyTakenException("Username already taken");
        }
    }

    public void checkIfEmailExists(String email) {
        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exists");
        }
    }
}
