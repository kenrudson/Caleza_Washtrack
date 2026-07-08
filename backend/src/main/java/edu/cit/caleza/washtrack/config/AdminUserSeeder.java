package edu.cit.caleza.washtrack.config;

import edu.cit.caleza.washtrack.entity.User;
import edu.cit.caleza.washtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@washtrack.com")) {
            User admin = User.builder()
                    .fullName("WashTrack Admin")
                    .email("admin@washtrack.com")
                    .phone("1234567890")
                    .address("WashTrack Shop")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.STAFF)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println("----------------------------------------------");
            System.out.println("SEEDED SUPERUSER: admin@washtrack.com / admin123");
            System.out.println("----------------------------------------------");
        }
    }
}
