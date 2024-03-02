package com.secureauthenticationapp.authenticationapp.unitTest.service;

import com.secureauthenticationapp.authenticationapp.domain.bean.AuthenticationRequest;
import com.secureauthenticationapp.authenticationapp.domain.bean.UserRegistration;
import com.secureauthenticationapp.authenticationapp.domain.entity.UserEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.UserAuthenticationException;
import com.secureauthenticationapp.authenticationapp.domain.repository.UserRepository;
import com.secureauthenticationapp.authenticationapp.domain.service.UserService;
import com.secureauthenticationapp.authenticationapp.utils.ServiceValidationUtil;
import com.secureauthenticationapp.authenticationapp.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ServiceValidationUtil serviceValidationUtil;
    @Mock
    private PasswordValidator passwordValidator;
    @InjectMocks
    private UserService userService;
    private UserEntity userEntity;
    private UserRegistration userRegistration;
    private AuthenticationRequest authenticationRequest;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setPassword("encodedPassword");
        userEntity.setFailedLoginAttempts(0);

        userRegistration = new UserRegistration();
        userRegistration.setUsername("testUser");
        userRegistration.setPassword("password");
        userRegistration.setEmail("test@example.com");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("testUser");
        authenticationRequest.setPassword("password");
    }

    @Test
    void registerUser_success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity registeredUser = userService.registerUser(userRegistration);

        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void registerUser_usernameAlreadyExists_throwsException() {
        doThrow(new UserAuthenticationException("Username already exists"))
                .when(serviceValidationUtil).checkIfUserNameExists(anyString());

        assertThrows(UserAuthenticationException.class, () -> userService.registerUser(userRegistration));
    }

    @Test
    void registerUser_emailAlreadyExists_throwsException() {
        doThrow(new UserAuthenticationException("Email already exists"))
                .when(serviceValidationUtil).checkIfEmailExists(anyString());

        assertThrows(UserAuthenticationException.class, () -> userService.registerUser(userRegistration));
    }

    @Test
    void registerUser_invalidPassword_throwsException() {
        doThrow(new UserAuthenticationException("Invalid password"))
                .when(passwordValidator).validatePassword(anyString());

        assertThrows(UserAuthenticationException.class, () -> userService.registerUser(userRegistration));
    }

    @Test
    void registerUser_repositorySaveFailure_throwsException() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(UserAuthenticationException.class, () -> userService.registerUser(userRegistration));
    }

    @Test
    void authenticateUser_success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String token = userService.authenticateUser(authenticationRequest);

        assertNotNull(token);
        assertTrue(userService.isTokenValid(token));
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void authenticateUser_accountLocked_throwsException() {
        userEntity.setLockTime(new Date());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        assertThrows(UserAuthenticationException.class, () -> userService.authenticateUser(authenticationRequest));
    }

    @Test
    void authenticateUser_exceedingFailedAttempts_locksAccount() {
        userEntity.setFailedLoginAttempts(MAX_FAILED_ATTEMPTS - 1);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UserAuthenticationException.class, () -> userService.authenticateUser(authenticationRequest));
        assertEquals(MAX_FAILED_ATTEMPTS, userEntity.getFailedLoginAttempts());
        assertNotNull(userEntity.getLockTime());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void authenticateUser_invalidCredentials_throwsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UserAuthenticationException.class, () -> userService.authenticateUser(authenticationRequest));
    }

    @Test
    void logoutUser_success() {
        String token = UUID.randomUUID().toString();
        userService.getTokenStore().put(token, "testUser");

        userService.logoutUser(token);

        assertFalse(userService.isTokenValid(token));
    }

    @Test
    void logoutUser_invalidToken_throwsException() {
        String token = UUID.randomUUID().toString();

        assertThrows(UserAuthenticationException.class, () -> userService.logoutUser(token));
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = UUID.randomUUID().toString();
        userService.getTokenStore().put(token, "testUser");

        assertTrue(userService.isTokenValid(token));
    }

    @Test
    void isTokenValid_invalidToken_returnsFalse() {
        String token = UUID.randomUUID().toString();

        assertFalse(userService.isTokenValid(token));
    }
}
