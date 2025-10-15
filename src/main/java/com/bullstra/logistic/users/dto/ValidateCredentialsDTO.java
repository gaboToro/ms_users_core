package com.bullstra.logistic.users.dto;

import lombok.Data;

// DTO used to validate user login credentials
@Data
public class ValidateCredentialsDTO {
    // User's email input for login
    private String email;

    // User's password input for login
    private String password;
}
