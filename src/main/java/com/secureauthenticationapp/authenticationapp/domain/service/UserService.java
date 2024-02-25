package com.secureauthenticationapp.authenticationapp.domain.service;

import com.secureauthenticationapp.authenticationapp.domain.bean.AuthenticationRequest;
import com.secureauthenticationapp.authenticationapp.domain.bean.UserRegistration;
import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.UserAuthenticationException;
import com.secureauthenticationapp.authenticationapp.domain.repository.UserRepository;
import com.secureauthenticationapp.authenticationapp.utils.ServiceValidationUtil;
import com.secureauthenticationapp.authenticationapp.validation.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class UserService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 30 * 60 * 1000;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceValidationUtil serviceValidationUtil;
    private final PasswordValidator passwordValidator;
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    @Transactional
    public UserEntity registerUser(UserRegistration userRegistration) {
        serviceValidationUtil.checkIfUserNameExists(userRegistration.getUsername());
        serviceValidationUtil.checkIfEmailExists(userRegistration.getEmail());
        passwordValidator.validatePassword(userRegistration.getPassword());
        try {
            UserEntity newUser = UserEntity.builder()
                    .username(userRegistration.getUsername())
                    .email(userRegistration.getEmail())
                    .password(passwordEncoder.encode(userRegistration.getPassword()))
                    .firstName(userRegistration.getFirstName())
                    .lastName(userRegistration.getLastName())
                    .build();
            log.info("New user: {}", newUser + " created successfully");
            return userRepository.save(newUser);
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
            throw new UserAuthenticationException("Failed to create user: " + e.getMessage());
        }
    }

    //@Transactional(dontRollbackOn = {UserAuthenticationException.class})
    public String authenticateUser(AuthenticationRequest authenticationRequest) {
        UserEntity user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (user.getLockTime() != null && new Date().getTime() - user.getLockTime().getTime() < LOCK_TIME_DURATION) {
            throw new UserAuthenticationException("Account is locked. Please try again later.");
        }
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            int failedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(failedAttempts);
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockTime(new Date());
                userRepository.save(user);
                log.warn("Account locked due to too many failed attempts: {}", user);
                throw new UserAuthenticationException("Account is locked due to too many failed attempts.");
            }
            userRepository.save(user);
            throw new UserAuthenticationException("Invalid username or password.");
        }
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getUsername());
        log.info("User: {} authenticated successfully", user);
        return token;
    }

    public void logoutUser(String token) {
        try {
            if(!tokenStore.containsKey(token)) {
                throw new UserAuthenticationException("Invalid token");
            }
            tokenStore.remove(token);
            log.info("User logged out successfully");
        } catch (Exception e) {
            log.error("Failed to logout user: {}", e.getMessage());
            throw new UserAuthenticationException("Failed to logout user: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token) {
        return tokenStore.containsKey(token);
    }

}
