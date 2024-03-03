package com.secureauthenticationapp.authenticationapp.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 30;
    private final Set<String> commonPasswords = loadCommonPasswords();

    private Set<String> loadCommonPasswords() {
        try (InputStream is = PasswordValidator.class.getResourceAsStream("/10000mostCommonPassword.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load common passwords list", e);
        }
    }

    private boolean isCommonPassword(String password) {
        return commonPasswords.contains(password);
    }

    public void validatePassword(String password) {
        // Check for null or empty
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // Length check
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Password must be between 8 and 30 characters long.");
        }

        // Common password check
        if (isCommonPassword(password)) {
            throw new IllegalArgumentException("Password is too common.");
        }
    }
}