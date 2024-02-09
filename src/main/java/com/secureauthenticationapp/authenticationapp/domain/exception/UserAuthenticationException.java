package com.secureauthenticationapp.authenticationapp.domain.exception;

public class UserAuthenticationException extends RuntimeException{
    public UserAuthenticationException(String message) {
        super(message);
    }
    public UserAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}