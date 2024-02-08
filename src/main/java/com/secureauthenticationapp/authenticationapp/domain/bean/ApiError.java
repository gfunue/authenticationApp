package com.secureauthenticationapp.authenticationapp.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
    private int statusCode;
    private String developerMessage;
    private int errorCode;
    private List<ValidationErrorResponse> validationErrors;
}
