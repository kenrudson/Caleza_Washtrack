package edu.cit.caleza.washtrack.features.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}