package com.bullstra.logistic.users.controller;

import com.bullstra.logistic.users.dto.LoginDTO;
import com.bullstra.logistic.users.dto.UserRegistrationDTO;
import com.bullstra.logistic.users.dto.UserResponseDTO;
import com.bullstra.logistic.users.model.User;
import com.bullstra.logistic.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // TODO: En un proyecto real, la delegación a otros microservicios se haría aquí
    // o en la capa de servicio, usando un cliente HTTP o un sistema de mensajería.

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO user = userService.registerUser(registrationDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) {
        Optional<User> user = userService.authenticateUser(loginDTO.getEmail(), loginDTO.getPassword());
        if (user.isPresent()) {
            // TODO: Delegar la generación de JWT al microservicio de autenticación.
            // Ejemplo:
            // String jwtToken = authService.generateToken(user.get().getId(), user.get().getRol().getRolName());
            // return ResponseEntity.ok(jwtToken);
            return ResponseEntity.ok("Login successful. JWT token placeholder.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}