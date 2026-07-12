package com.carehub.carehub.config;

import com.carehub.carehub.entity.User;
import com.carehub.carehub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a single default Admin account on first launch so there is always a
 * way into the system. Logs the generated/default credentials to the
 * console — change the password immediately after first login in a real
 * deployment.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .role("Admin")
                    .isActive(true)
                    .build();
            userRepository.save(admin);

            System.out.println("=================================================================");
            System.out.println(" CareHub: no users found — created a default Admin account.");
            System.out.println("   username: admin");
            System.out.println("   password: Admin@123");
            System.out.println("   Please sign in and change this password immediately.");
            System.out.println("=================================================================");
        }
    }
}
