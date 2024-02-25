package com.secureauthenticationapp.authenticationapp.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 30;
    private static final String COMMON_PASSWORDS_FILE = "src/main/resources/owaspCommonPassword/10000mostCommonPassword.txt";
    private static final Set<String> commonPasswords = loadCommonPasswords();

    private static Set<String> loadCommonPasswords() {
        try (Stream<String> lines = Files.lines(Paths.get(COMMON_PASSWORDS_FILE))) {
            return lines.collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load common passwords list", e);
        }
    }

    private static boolean isCommonPassword(String password) {
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