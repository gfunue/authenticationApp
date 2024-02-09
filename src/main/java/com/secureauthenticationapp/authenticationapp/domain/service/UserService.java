package com.secureauthenticationapp.authenticationapp.domain.service;

import com.secureauthenticationapp.authenticationapp.domain.bean.AuthenticationRequest;
import com.secureauthenticationapp.authenticationapp.domain.bean.Registration;
import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.UserAuthenticationException;
import com.secureauthenticationapp.authenticationapp.domain.repository.UserRepository;
import com.secureauthenticationapp.authenticationapp.utils.ServiceValidationUtil;
import com.secureauthenticationapp.authenticationapp.validation.PasswordValidator;
import jakarta.transaction.Transactional;
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
public class UserService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 30 * 60 * 1000;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceValidationUtil serviceValidationUtil;
    private final PasswordValidator passwordValidator;
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    @Transactional
    public UserEntity registerUser(Registration registration) {
        serviceValidationUtil.checkIfUserNameExists(registration.getUsername());
        serviceValidationUtil.checkIfEmailExists(registration.getEmail());
        passwordValidator.validatePassword(registration.getPassword());

        UserEntity newUser = UserEntity.builder()
                .username(registration.getUsername())
                .email(registration.getEmail())
                .password(passwordEncoder.encode(registration.getPassword()))
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .build();
        log.info("New user: {}", newUser + " created successfully");
        return userRepository.save(newUser);
    }

    @Transactional
    public String authenticateUser(AuthenticationRequest authenticationRequest) {
        UserEntity user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        // Check for account lockout
        if (user.getLockTime() != null && new Date().getTime() - user.getLockTime().getTime() < LOCK_TIME_DURATION) {
            throw new UserAuthenticationException("Account is locked. Please try again later.");
        }
        // Check password match
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            // Handle failed login attempt (increment counter, check for lockout, etc.)
            int failedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(failedAttempts);
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockTime(new Date()); // Lock the account
                userRepository.save(user);
                log.warn("Account locked due to too many failed attempts: {}", user);
                throw new UserAuthenticationException("Account is locked due to too many failed attempts.");
            }
            userRepository.save(user);
            throw new UserAuthenticationException("Invalid username or password.");
        }
        // If authentication is successful
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getUsername()); // Associate token with username
        log.info("User: {} authenticated successfully", user);
        return token;
    }

    public void logoutUser(String token) {
        tokenStore.remove(token);
    }

    public boolean isTokenValid(String token) {
        return tokenStore.containsKey(token);
    }

}
