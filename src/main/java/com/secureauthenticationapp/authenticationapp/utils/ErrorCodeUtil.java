package com.secureauthenticationapp.authenticationapp.utils;

import com.secureauthenticationapp.authenticationapp.domain.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

public class ErrorCodeUtil {

    private ErrorCodeUtil(){}

    private static final Map<Class<? extends Throwable>, Integer> STANDARD_EXCEPTION_ERROR_CODES = Map.ofEntries(

            Map.entry(EmailAlreadyExistException.class, 100001),
            Map.entry(UsernameAlreadyTakenException.class, 10002),
            Map.entry(UserAuthenticationException.class, 10003),
            Map.entry(CustomFileUploadException.class, 10004),
            Map.entry(FileDeleteException.class, 10005),
            Map.entry(TokenValidationException.class, 10006),
            Map.entry(BlogNotFoundException.class, 10007),
            Map.entry(BlogOperationException.class, 10008),

            Map.entry(MethodArgumentNotValidException.class, 200001),
            Map.entry(ConstraintViolationException.class, 200002),
            Map.entry(EntityNotFoundException.class, 200003),
            Map.entry(IllegalArgumentException.class, 200004),
            Map.entry(NullPointerException.class, 200005),
            Map.entry(Exception.class, 200006),
            Map.entry(IllegalStateException.class, 200007),
            Map.entry(UsernameNotFoundException.class, 200008),
            Map.entry(ServletException.class, 200009)
    );

    public static int getErrorCodeForException(Throwable ex) {
        return STANDARD_EXCEPTION_ERROR_CODES.getOrDefault(ex.getClass(), 0);
    }
}
