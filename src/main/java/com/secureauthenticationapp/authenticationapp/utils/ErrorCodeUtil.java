package com.secureauthenticationapp.authenticationapp.utils;

import com.secureauthenticationapp.authenticationapp.domain.exception.EmailAlreadyExistException;
import com.secureauthenticationapp.authenticationapp.domain.exception.UserAuthenticationException;
import com.secureauthenticationapp.authenticationapp.domain.exception.UsernameAlreadyTakenException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

public class ErrorCodeUtil {

    private ErrorCodeUtil(){}

    private static final Map<Class<? extends Throwable>, Integer> STANDARD_EXCEPTION_ERROR_CODES = Map.ofEntries(

            Map.entry(EmailAlreadyExistException.class, 100003),
            Map.entry(UsernameAlreadyTakenException.class, 100018),
            Map.entry(UserAuthenticationException.class, 100019),

            Map.entry(MethodArgumentNotValidException.class, 200001),
            Map.entry(ConstraintViolationException.class, 200002),
            Map.entry(EntityNotFoundException.class, 200003),
            Map.entry(IllegalArgumentException.class, 200004),
            Map.entry(NullPointerException.class, 200005),
            Map.entry(Exception.class, 200006),
            Map.entry(IllegalStateException.class, 200007),
            Map.entry(UsernameNotFoundException.class, 200008)
    );

    public static int getErrorCodeForException(Throwable ex) {
        return STANDARD_EXCEPTION_ERROR_CODES.getOrDefault(ex.getClass(), 0);
    }
}
