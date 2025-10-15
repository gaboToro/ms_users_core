package com.bullstra.logistic.users.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

// Entity representing a user in the system
@Data
@Entity
@Table(name = "users")
public class User {

    // Primary key, generated as UUID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // User's first name
    @Column(name = "first_name", nullable = false)
    private String name;

    // User's last name
    @Column(name = "last_name", nullable = false)
    private String lastName;

    // User's email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Hashed password for authentication
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Many-to-one relationship to Rol entity
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Rol rol;

    // Timestamp when the user was created
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    // Timestamp of the user's last session
    @Column(name = "last_session")
    private LocalDateTime lastSession;

    // Automatically sets creationDate before persisting
    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}