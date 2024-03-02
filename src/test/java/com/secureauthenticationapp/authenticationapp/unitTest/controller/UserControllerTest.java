package com.secureauthenticationapp.authenticationapp.unitTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secureauthenticationapp.authenticationapp.domain.bean.AuthenticationRequest;
import com.secureauthenticationapp.authenticationapp.domain.bean.UserRegistration;
import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.UserAuthenticationException;
import com.secureauthenticationapp.authenticationapp.domain.service.UserService;
import com.secureauthenticationapp.authenticationapp.web.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_success() throws Exception {
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setUsername("testUser");
        userRegistration.setPassword("Any_password");
        userRegistration.setEmail("test@example.com");

        UserEntity newUser = new UserEntity();
        newUser.setUserId(1L);
        newUser.setUsername(userRegistration.getUsername());
        newUser.setEmail(userRegistration.getEmail());

        when(userService.registerUser(any(UserRegistration.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").value(newUser.getUserId()))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }


    @Test
    void registerUser_missingField_failure() throws Exception {
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setUsername("testUser");

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistration)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticateUser_success() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("testUser");
        authenticationRequest.setPassword("correctPassword");

        String token = "testToken";
        when(userService.authenticateUser(any(AuthenticationRequest.class))).thenReturn(token);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value(token))
                .andExpect(jsonPath("$.message").value("Authentication successful"));
    }

    @Test
    void authenticateUser_invalidCredentials() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("testUser");
        authenticationRequest.setPassword("wrongPassword");

        when(userService.authenticateUser(any(AuthenticationRequest.class)))
                .thenThrow(new UserAuthenticationException("Invalid username or password"));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void authenticateUser_accountLocked() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("lockedUser");
        authenticationRequest.setPassword("password");

        when(userService.authenticateUser(any(AuthenticationRequest.class)))
                .thenThrow(new UserAuthenticationException("Account is locked. Please try again later."));

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account is locked. Please try again later."));
    }

    @Test
    void logoutUser_success() throws Exception {
        String token = "Bearer testToken";
        doNothing().when(userService).logoutUser(anyString());

        mockMvc.perform(delete("/api/v1/users/logout")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully"));
    }

    @Test
    void logoutUser_invalidToken() throws Exception {
        String token = "Bearer invalidToken";
        doThrow(new UserAuthenticationException("Invalid token")).when(userService).logoutUser(anyString());

        mockMvc.perform(delete("/api/v1/users/logout")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token"));
    }
}

