package com.bullstra.logistic.users.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private LocalDateTime creationDate;
    private LocalDateTime lastSession;
    private String rolName;
}