package com.bullstra.logistic.users.dto;

import lombok.Data;

// DTO used for user login requests
@Data
public class LoginDTO {

    // User email for authentication
    private String email;

    // User password for authentication
    private String password;
}
