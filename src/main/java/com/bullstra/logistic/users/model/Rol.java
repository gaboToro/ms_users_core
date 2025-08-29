package com.bullstra.logistic.users.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name_rol", unique = true, nullable = false)
    private String rolName;

    @Column(name = "description")
    private String description;

}