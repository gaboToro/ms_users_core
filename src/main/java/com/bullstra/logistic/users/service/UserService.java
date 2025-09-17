package com.bullstra.logistic.users.service;

import com.bullstra.logistic.users.dto.UserRegistrationDTO;
import com.bullstra.logistic.users.dto.UserResponseDTO;
import com.bullstra.logistic.users.model.Rol;
import com.bullstra.logistic.users.model.User;
import com.bullstra.logistic.users.repository.RolRepository;
import com.bullstra.logistic.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setLastName(registrationDTO.getLastName());
        user.setEmail(registrationDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));

        Rol rol = rolRepository.findByRolName("client")
                .orElseThrow(() -> new IllegalStateException("Default role 'client' not found"));
        user.setRol(rol);

        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    public Optional<User> authenticateUser (String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPasswordHash())) {
            User user = userOptional.get();
            user.setLastSession(LocalDateTime.now());
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public UserResponseDTO updateUser (UUID userId, UserRegistrationDTO updateDTO, String requesterEmail) throws IllegalAccessException {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found"));
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User  to update not found"));
        // Restricciones de rol
        if ("logistic_staff".equals(requester.getRol().getRolName())) {
            throw new IllegalAccessException("Logistic Staff cannot modify any user");
        }
        if (!"admin".equals(requester.getRol().getRolName()) && !requester.getId().equals(userId)) {
            throw new IllegalAccessException("Only admin can modify other users");
        }

        // No permitir modificar email
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(userToUpdate.getEmail())) {
            throw new IllegalArgumentException("Email cannot be modified");
        }
        // Actualizar campos permitidos
        if (updateDTO.getName() != null) userToUpdate.setName(updateDTO.getName());
        if (updateDTO.getLastName() != null) userToUpdate.setLastName(updateDTO.getLastName());
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            userToUpdate.setPasswordHash(passwordEncoder.encode(updateDTO.getPassword()));
        }
        // Actualizar rol solo si es admin y se especifica rolName
        if ("admin".equals(requester.getRol().getRolName()) && updateDTO.getRolName() != null) {
            Rol newRol = rolRepository.findByRolName(updateDTO.getRolName())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + updateDTO.getRolName()));
            userToUpdate.setRol(newRol);
        }

        User savedUser  = userRepository.save(userToUpdate);
        return mapToResponseDTO(savedUser );
    }

    public void deleteUser (UUID userId, String requesterEmail) throws IllegalAccessException {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found"));
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User  to delete not found"));
        if ("logistic_staff".equals(requester.getRol().getRolName())) {
            throw new IllegalAccessException("Logistic staff cannot delete any user");
        }
        if (!"admin".equals(requester.getRol().getRolName()) && !requester.getId().equals(userId)) {
            throw new IllegalAccessException("Only admin can delete other users");
        }
        userRepository.delete(userToDelete);
    }

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

        String rolName = registrationDTO.getRolName() != null ? registrationDTO.getRolName() : "client";
        Rol rol = rolRepository.findByRolName(rolName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + rolName));
        user.setRol(rol);
        User savedUser  = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

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
}