package com.bullstra.logistic.users.controller;

import com.bullstra.logistic.users.dto.*;
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

    // Internal endpoint for Auth Service to get authentication data
    @GetMapping("/internal/auth-data")
    public ResponseEntity<?> getAuthData(@RequestParam String email) {
        Optional<UserAuthDataDTO> authData = userService.getAuthDataByEmail(email);
        if (authData.isPresent()) {
            return ResponseEntity.ok(authData.get());
        } else {
            // Return 404 without revealing user existence
            return ResponseEntity.notFound().build();
        }
    }

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserResponseDTO user = userService.registerUser(registrationDTO);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update user data by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id,
                                        @RequestBody UserRegistrationDTO updateDTO,
                                        @RequestParam String requesterEmail) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, updateDTO, requesterEmail);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id,
                                        @RequestParam String requesterEmail) {
        try {
            userService.deleteUser(id, requesterEmail);
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Create user with role (admin only)
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

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<UserResponseDTO> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}