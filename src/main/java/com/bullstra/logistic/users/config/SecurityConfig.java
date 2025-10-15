package com.bullstra.logistic.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Configures the main security filter chain for HTTP requests.
     * Defines which endpoints are publicly accessible and which require authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (useful for APIs and testing environments)
                .csrf(csrf -> csrf.disable())

                // Configure access rules for different HTTP requests
                .authorizeHttpRequests(authorize -> authorize

                        // Allow registration and login endpoints without authentication
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                        // Allow admin user creation endpoint without authentication
                        .requestMatchers(HttpMethod.POST, "/api/users/admin/create").permitAll()

                        // Allow all GET requests for user data (for testing or public access)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                        // NEW RULES: Allow PUT and DELETE for testing purposes
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()

                        // For now, no endpoint requires authentication, but the rule remains
                        // for clarity and future security hardening.
                        .anyRequest().authenticated()
                );

        // Build and return the configured security filter chain
        return http.build();
    }
}
