package com.bullstra.logistic.users.dto;

import lombok.Data;
import java.util.UUID;

// DTO used to transfer authentication-related user data
@Data
public class UserAuthDataDTO {
    // User's unique identifier
    private UUID id;

    // User's email address
    private String email;

    // Hashed password for authentication
    private String passwordHash;

    // Role assigned to the user
    private String rolName;
}