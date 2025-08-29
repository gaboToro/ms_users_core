package com.bullstra.logistic.users.repository;

import com.bullstra.logistic.users.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByRolName(String rolName);
}