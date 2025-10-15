package com.bullstra.logistic.users.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
// DTO used to send user information in API responses
public class UserResponseDTO {
    // Unique identifier for the user
    private UUID id;

    // User's first name
    private String name;

    // User's last name
    private String lastName;

    // User's email address
    private String email;

    // Timestamp when the user was created
    private LocalDateTime creationDate;

    // Timestamp of the user's last session
    private LocalDateTime lastSession;

    // Role assigned to the user
    private String rolName;
}