package com.bullstra.logistic.users.model;

import jakarta.persistence.*;
import lombok.Data;

// Entity representing a user role in the system
@Data
@Entity
@Table(name = "roles")
public class Rol {

    // Primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Name of the role, must be unique and not null
    @Column(name = "name_rol", unique = true, nullable = false)
    private String rolName;

    // Optional description of the role
    @Column(name = "description")
    private String description;
}