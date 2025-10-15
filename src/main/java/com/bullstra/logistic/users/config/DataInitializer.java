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

    //Creates a new role in the database if it does not already exist.
    private void createRoleIfNotExists(String rolName, String description) {
        // Check if the role with the given name already exists in the database
        if (rolRepository.findByRolName(rolName).isEmpty()) {
            Rol rol = new Rol();
            rol.setRolName(rolName);
            rol.setDescription(description);
            // Save the new role to the database
            rolRepository.save(rol);
        }
    }

    //It initializes default roles and creates an admin user if it doesn't exist.
    @Override
    public void run(String... args) throws Exception {
        // Ensure that the default roles exist
        createRoleIfNotExists("admin", "Staff responsible for managing the system");
        createRoleIfNotExists("client", "End user of the system who places orders");
        createRoleIfNotExists("logistic_staff", "Warehouse staff and drivers");

        // Check if the default admin user already exists
        Optional<User> adminUser = userRepository.findByEmail("admin@bullstra.com");

        if (adminUser.isEmpty()) {
            // Retrieve the admin role from the database, or throw an error if not found
            Rol adminRol = rolRepository.findByRolName("admin")
                    .orElseThrow(() -> new IllegalStateException("Admin role not found"));

            // Create the default admin user
            User admin = new User();
            admin.setName("Admin");
            admin.setLastName("Bullstra");
            admin.setRol(adminRol);
            admin.setEmail("admin@bullstra.com");
            // Encrypt the password before saving
            admin.setPasswordHash(passwordEncoder.encode("admin123"));

            // Save the admin user to the database
            userRepository.save(admin);

            // Log to the console that the admin user has been created
            System.out.println("Admin user has been created | admin@bullstra.com : admin123");
        }
    }
}
