package com.bullstra.logistic.users.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}