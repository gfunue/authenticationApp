package com.secureauthenticationapp.authenticationapp.domain.bean;

import lombok.Data;

@Data
public class UserRegistration {
    private String username;
    private String email;
    private String password;
}
