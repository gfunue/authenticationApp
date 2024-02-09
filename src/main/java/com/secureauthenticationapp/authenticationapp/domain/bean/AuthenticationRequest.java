package com.secureauthenticationapp.authenticationapp.domain.bean;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
