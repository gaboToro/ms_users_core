package com.bullstra.logistic.users.config;

import com.bullstra.logistic.users.model.Rol;
import com.bullstra.logistic.users.model.User;
import com.bullstra.logistic.users.repository.RolRepository;
import com.bullstra.logistic.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private void createRoleIfNotExists(String rolName, String description) {
        if (rolRepository.findByRolName(rolName).isEmpty()) {
            Rol rol = new Rol();
            rol.setRolName(rolName);
            rol.setDescription(description);
            rolRepository.save(rol);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExists("admin", "Staff responsible for managing the system");
        createRoleIfNotExists("client", "End user of the system who places orders");
        createRoleIfNotExists("logistic_staff", "Warehouse staff and drivers");

        Optional<User> adminUser = userRepository.findByEmail("admin@bullstra.com");
        if (adminUser.isEmpty()){
            Rol adminRol = rolRepository.findByRolName("admin").orElseThrow(() -> new IllegalStateException("Admin user not found"));

            User admin = new User();
            admin.setName("Admin");
            admin.setLastName("Bullstra");
            admin.setRol(adminRol);
            admin.setEmail("admin@bullstra.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            userRepository.save(admin);

            System.out.println("Admin user has been created | admin@bullstra.com : admin123");
        }
    }
}