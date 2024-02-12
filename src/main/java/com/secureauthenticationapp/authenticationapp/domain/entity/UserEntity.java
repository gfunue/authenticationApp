package com.secureauthenticationapp.authenticationapp.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
@Entity
@Table(name = "userEntity")
public class UserEntity {

    /**
     * Represents the unique identifier for the user.
     * This ID is auto-generated and cannot be empty.
     * Maps to the "userId" column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false, updatable = false)
    private Long userId;

    /**
     * Represents the user's username.
     * Must be unique and cannot be empty.
     * Maps to the "userName" column in the database.
     */
    @Size(min = 2, max = 20, message = "UserEntity name must be between 2 and 20 characters long")
    @NotBlank(message = "UserEntity name field cannot be empty")
    @Column(name = "username", unique = true, nullable = false)
    @Pattern(regexp = "[a-zA-Z0-9]*", message = "Username can only contain alphanumeric characters")
    private String username;

    /**
     * The user's email, must be unique.
     * This field is mapped to the database column "email".
     * Emails must be at most 40 characters long and must be a valid email address.
     */
    @Size(max = 40, message = "Email must be less than 40 characters long")
    @NotBlank(message = "Email field cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /**
     * The user's password, cannot be empty.
     * This field is mapped to the database column "password".
     * Passwords must be at least 8 characters long and cannot be longer than 80 characters.
     */
    //@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
    @NotBlank(message = "Password field cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The user's first name.
     * This field is mapped to the database column "firstName".
     * First names must be between 2 and 30 characters long.
     */
    @Size(min = 2, max = 30, message = "First name must be between 2 and 80 characters long")
    @Column(name = "firstName")
    private String firstName;

    /**
     * The user's middle initial.
     * This field is mapped to the database column "middleInitial".
     * Middle initials must be at most 1 character long.
     */
    @Size(max = 1, min = 1, message = "Middle initial must be at most 1 character long")
    @Column(name = "middleInitial")
    private String middleInitial;

    /**
     * The user's last name.
     * This field is mapped to the database column "lastName".
     * Last names must be between 2 and 30 characters long.
     */
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 80 characters")
    @Column(name = "lastName")
    private String lastName;

    /**
     * Number of failed login attempts for the user.
     */
    @Builder.Default
    @Column(name = "failedLoginAttempts", columnDefinition = "int default 0")
    @Min(value = 0, message = "Failed login attempts cannot be negative")
    private Integer failedLoginAttempts = 0;

    /**
     * The time the user's account was locked.
     */
    @Column(name = "lockTime")
    private Date lockTime;
}
