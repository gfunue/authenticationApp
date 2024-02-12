package com.secureauthenticationapp.authenticationapp.domain.exception;

import com.secureauthenticationapp.authenticationapp.domain.bean.ApiError;
import com.secureauthenticationapp.authenticationapp.domain.bean.ValidationErrorResponse;
import com.secureauthenticationapp.authenticationapp.utils.ErrorCodeUtil;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Object> handleEmailAlreadyExist(EmailAlreadyExistException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<Object> handleEmailNotFound(UsernameAlreadyTakenException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<Object> handleUserAuthenticationException(UserAuthenticationException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(CustomFileUploadException.class)
    public ResponseEntity<Object> handleFileUploadException(CustomFileUploadException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<Object> handleTokenValidationException(TokenValidationException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(FileDeleteException.class)
    public ResponseEntity<Object> handleFileDeleteException(FileDeleteException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Object> handleServletException(ServletException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BlogNotFoundException.class)
    public ResponseEntity<Object> handleBlogNotFoundException(BlogNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

//    @ExceptionHandler(BlogOperationException.class)
//    public ResponseEntity<Object> handleBlogOperationException(BlogOperationException ex) {
//        ApiError apiError = ApiError.builder()
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message(ex.getMessage())
//                .timestamp(LocalDateTime.now())
//                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
//                .build();
//        return buildResponseEntity(apiError);
//    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> handleException(Exception ex) {
//        ApiError apiError = ApiError.builder()
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message("An unexpected error occurred. Please try again later.")
//                .timestamp(LocalDateTime.now())
//                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
//                .build();
//        return buildResponseEntity(apiError);
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ValidationErrorResponse> errors = ex.getConstraintViolations().stream()
                .map(violation -> new ValidationErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .toList();

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .validationErrors(errors)
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .errorCode(ErrorCodeUtil.getErrorCodeForException(ex))
                .build();
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(apiError, headers, apiError.getStatus());
    }
}
