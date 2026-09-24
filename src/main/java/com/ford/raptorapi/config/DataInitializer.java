package com.ford.raptorapi.config;

import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.model.enums.UserRole;
import com.ford.raptorapi.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cria os usuários iniciais da aplicação (seed) caso ainda não existam.
 * Os hashes BCrypt são gerados em runtime via PasswordEncoder, garantindo
 * senhas sempre válidas para a tabela app_users.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("admin@ford.com.br", "password", "Consultor Teste", "Ford Matriz", UserRole.CONSULTOR);
        seedUser("admin@raptor.com.br", "admin123", "Administrador Sistema", "Ford HQ", UserRole.ADMIN);
    }

    private void seedUser(String email, String rawPassword, String name, String dealership, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        AppUser user = new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setDealership(dealership);
        user.setRole(role);
        userRepository.save(user);
        log.info("Seed user created: {} ({})", email, role);
    }
}