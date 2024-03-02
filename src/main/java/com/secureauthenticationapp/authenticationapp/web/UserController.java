package com.secureauthenticationapp.authenticationapp.web;

import com.secureauthenticationapp.authenticationapp.domain.bean.AuthenticationRequest;
import com.secureauthenticationapp.authenticationapp.domain.bean.HttpResponse;
import com.secureauthenticationapp.authenticationapp.domain.bean.UserRegistration;
import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.UserAuthenticationException;
import com.secureauthenticationapp.authenticationapp.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerUser(@Valid @RequestBody UserRegistration userRegistration) {
        try {
            UserEntity newUser = userService.registerUser(userRegistration);
            if (newUser == null) {
                throw new UserAuthenticationException("User registration failed");
            }
            HttpResponse response = HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.CREATED.value())
                    .status(HttpStatus.CREATED)
                    .reason(HttpStatus.CREATED.getReasonPhrase())
                    .message("User registered successfully")
                    .developerMessage("User registration completed")
                    .data(Collections.singletonMap("userId", newUser.getUserId()))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserAuthenticationException e) {
            HttpResponse errorResponse = HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(e.getMessage())
                    .developerMessage("User registration failed")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        String token = userService.authenticateUser(authenticationRequest);
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .reason(HttpStatus.OK.getReasonPhrase())
                .message("Authentication successful")
                .developerMessage("Authentication processed")
                .data(new HashMap<String, String>() {{
                    put("token", token);
                }})
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<HttpResponse> logoutUser(@Valid @RequestHeader("Authorization") String authHeader) {
        final String token = authHeader.substring(7);
        userService.logoutUser(token);
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .reason(HttpStatus.OK.getReasonPhrase())
                .message("User logged out successfully")
                .developerMessage("Logout processed")
                .data(Collections.emptyMap())
                .build();
        return ResponseEntity.ok(response);
    }
}
