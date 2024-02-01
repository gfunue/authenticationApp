package com.secureauthenticationapp.authenticationapp.domain.bean;

import lombok.Data;

@Data
public class Registration {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
