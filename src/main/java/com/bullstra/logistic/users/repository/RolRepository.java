package com.bullstra.logistic.users.repository;

import com.bullstra.logistic.users.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository interface for Rol entity operations
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Finds a role by its name, returns Optional
    Optional<Rol> findByRolName(String rolName);
}
