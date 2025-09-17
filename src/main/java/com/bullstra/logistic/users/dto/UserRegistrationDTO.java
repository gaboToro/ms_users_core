package com.bullstra.logistic.users.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String rolName;
}