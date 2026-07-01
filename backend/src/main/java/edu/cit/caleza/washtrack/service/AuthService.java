package edu.cit.caleza.washtrack.service;

import edu.cit.caleza.washtrack.dto.*;
import edu.cit.caleza.washtrack.entity.User;
import edu.cit.caleza.washtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        // BR-001: Account Uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // NFR-003
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        String token = generateToken(saved);

        return new AuthResponse(token, saved.getUserId(), saved.getFullName(),
                saved.getEmail(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = generateToken(user);

        return new AuthResponse(token, user.getUserId(), user.getFullName(),
                user.getEmail(), user.getRole().name());
    }

    // Simple session token for now (FR-002 just requires a token to be generated).
    // Swap for real JWT later once auth basics are working end-to-end.
    private String generateToken(User user) {
        return UUID.randomUUID().toString();
    }
}
