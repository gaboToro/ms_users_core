package com.bullstra.logistic.users.dto;

import lombok.Data;

// DTO used for registering a new user
@Data
public class UserRegistrationDTO {

    // User's first name
    private String name;

    // User's last name
    private String lastName;

    // User's email address
    private String email;

    // User's password
    private String password;

    // Role name assigned to the user
    private String rolName;
}