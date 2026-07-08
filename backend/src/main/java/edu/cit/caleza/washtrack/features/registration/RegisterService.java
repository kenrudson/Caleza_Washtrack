package edu.cit.caleza.washtrack.features.registration;

import edu.cit.caleza.washtrack.shared.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final RegistrationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        String token = UUID.randomUUID().toString();

        return new RegisterResponse(token, saved.getUserId(), saved.getFullName(),
                saved.getEmail(), saved.getRole().name());
    }
}