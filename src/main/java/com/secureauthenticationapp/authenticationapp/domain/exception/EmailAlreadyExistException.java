package com.secureauthenticationapp.authenticationapp.domain.exception;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException(String message) {
        super(message);
    }

}
