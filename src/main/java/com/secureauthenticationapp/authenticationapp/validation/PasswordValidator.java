package com.secureauthenticationapp.authenticationapp.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PasswordValidator {
    private static final String COMMON_PASSWORDS_FILE = "src/main/resources/owaspCommonPassword/10000mostCommonPassword.txt";
    private static final Set<String> commonPasswords = loadCommonPasswords();

    private static Set<String> loadCommonPasswords() {
        try {
            return Files.lines(Paths.get(COMMON_PASSWORDS_FILE)).collect(Collectors.toSet());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load common passwords list", e);
        }
    }

    private static boolean isCommonPassword(String password) {
        return commonPasswords.contains(password);
    }
}
