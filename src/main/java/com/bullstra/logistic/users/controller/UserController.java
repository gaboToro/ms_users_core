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
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser (@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserResponseDTO user = userService.registerUser (registrationDTO);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser (@PathVariable UUID id,
                                         @RequestBody UserRegistrationDTO updateDTO,
                                         @RequestParam String requesterEmail) {
        try {
            UserResponseDTO updatedUser  = userService.updateUser (id, updateDTO, requesterEmail);
            return ResponseEntity.ok(updatedUser );
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser (@PathVariable UUID id,
                                         @RequestParam String requesterEmail) {
        try {
            userService.deleteUser (id, requesterEmail);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/create")
    public ResponseEntity<?> createUserByAdmin(@RequestBody UserRegistrationDTO registrationDTO,
                                                @RequestParam String requesterEmail) {
        try {
            UserResponseDTO user = userService.registerUserWithRole(registrationDTO, requesterEmail);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}