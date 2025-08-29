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
import java.util.Optional;

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

        // Asignar rol por defecto, en este caso 'cliente'
        Rol rol = rolRepository.findByRolName("client")
                .orElseThrow(() -> new IllegalStateException("Default role 'client' not found"));
        user.setRol(rol);

        User savedUser = userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(savedUser.getId());
        responseDTO.setName(savedUser.getName());
        responseDTO.setLastName(savedUser.getLastName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setRolName(savedUser.getRol().getRolName());
        responseDTO.setCreationDate(savedUser.getCreationDate());
        responseDTO.setLastSession(savedUser.getLastSession());

        return responseDTO;
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPasswordHash())) {
            // Actualizar la última sesión
            User user = userOptional.get();
            user.setLastSession(java.time.LocalDateTime.now());
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}