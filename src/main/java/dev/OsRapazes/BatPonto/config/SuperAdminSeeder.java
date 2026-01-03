package dev.OsRapazes.BatPonto.config;

import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.entity.enums.Role;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.email}")
    private String email;

    @Value("${app.superadmin.password}")
    private String password;

    @Value("${app.superadmin.name:Super Admin}")
    private String name;

    @Override
    public void run(String... args) {
        String normalizedEmail = email.toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            return; // já existe, não faz nada
        }

        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(normalizedEmail);
        user.setRole(Role.SUPERADMIN);
        user.setPasswordHash(passwordEncoder.encode(password));

        userRepository.save(user);
    }
}