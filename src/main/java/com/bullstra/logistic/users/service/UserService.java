package com.bullstra.logistic.users.service;

import com.bullstra.logistic.users.dto.UserAuthDataDTO;
import com.bullstra.logistic.users.dto.UserRegistrationDTO;
import com.bullstra.logistic.users.dto.UserResponseDTO;
import com.bullstra.logistic.users.model.Rol;
import com.bullstra.logistic.users.model.User;
import com.bullstra.logistic.users.repository.RolRepository;
import com.bullstra.logistic.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

// Service class handling user operations such as registration, updates, deletion, and authentication-related data
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registers a new user with default role 'client'
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if email is already used
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create user entity
        User user = new User();
        user.setName(registrationDTO.getName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));

        // Assign default role 'client'
        Rol rol = rolRepository.findByRolName("client")
                .orElseThrow(() -> new IllegalStateException("Default role 'client' not found"));
        user.setRol(rol);

        // Save user to database
        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    //Retrieves authentication data for internal use by Auth Service
    public Optional<UserAuthDataDTO> getAuthDataByEmail(String email) {
        return userRepository.findByEmail(email).map(user -> {
            UserAuthDataDTO dto = new UserAuthDataDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setPasswordHash(user.getPasswordHash());
            dto.setRolName(user.getRol().getRolName());
            return dto;
        });
    }

    // Updates an existing user's details with role and permission checks
    public UserResponseDTO updateUser(UUID userId, UserRegistrationDTO updateDTO, String requesterEmail) throws IllegalAccessException {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found"));
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User to update not found"));

        // Role restrictions
        if ("logistic_staff".equals(requester.getRol().getRolName())) {
            throw new IllegalAccessException("Logistic Staff cannot modify any user");
        }
        if (!"admin".equals(requester.getRol().getRolName()) && !requester.getId().equals(userId)) {
            throw new IllegalAccessException("Only admin can modify other users");
        }

        // Prevent email modification
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(userToUpdate.getEmail())) {
            throw new IllegalArgumentException("Email cannot be modified");
        }

        // Update allowed fields
        if (updateDTO.getName() != null) userToUpdate.setName(updateDTO.getName());
        if (updateDTO.getLastName() != null) userToUpdate.setLastName(updateDTO.getLastName());
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            userToUpdate.setPasswordHash(passwordEncoder.encode(updateDTO.getPassword()));
        }

        // Update role only if requester is admin
        if ("admin".equals(requester.getRol().getRolName()) && updateDTO.getRolName() != null) {
            Rol newRol = rolRepository.findByRolName(updateDTO.getRolName())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + updateDTO.getRolName()));
            userToUpdate.setRol(newRol);
        }

        User savedUser = userRepository.save(userToUpdate);
        return mapToResponseDTO(savedUser);
    }

    // Deletes a user with role and permission checks
    public void deleteUser(UUID userId, String requesterEmail) throws IllegalAccessException {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found"));
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User to delete not found"));

        if ("logistic_staff".equals(requester.getRol().getRolName())) {
            throw new IllegalAccessException("Logistic staff cannot delete any user");
        }
        if (!"admin".equals(requester.getRol().getRolName()) && !requester.getId().equals(userId)) {
            throw new IllegalAccessException("Only admin can delete other users");
        }

        userRepository.delete(userToDelete);
    }

    // Registers a new user with a specified role (admin-only operation)
    public UserResponseDTO registerUserWithRole(UserRegistrationDTO registrationDTO, String requesterEmail) throws IllegalAccessException {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found"));

        if (!"admin".equals(requester.getRol().getRolName())) {
            throw new IllegalAccessException("Only admin can create users with roles");
        }

        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));

        // Assign role or default to 'client'
        String rolName = registrationDTO.getRolName() != null ? registrationDTO.getRolName() : "client";
        Rol rol = rolRepository.findByRolName(rolName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + rolName));
        user.setRol(rol);

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    // Maps User entity to UserResponseDTO
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRolName(user.getRol().getRolName());
        dto.setCreationDate(user.getCreationDate());
        dto.setLastSession(user.getLastSession());
        return dto;
    }

    // Retrieves a user by ID and maps to UserResponseDTO
    public Optional<UserResponseDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::mapToResponseDTO);
    }
}