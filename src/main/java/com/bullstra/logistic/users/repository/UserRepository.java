package com.bullstra.logistic.users.repository;

import com.bullstra.logistic.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

// Repository interface for User entity operations
public interface UserRepository extends JpaRepository<User, UUID> {

    // Finds a user by email, returns Optional
    Optional<User> findByEmail(String email);
}