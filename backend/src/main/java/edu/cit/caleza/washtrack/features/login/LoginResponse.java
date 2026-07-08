package edu.cit.caleza.washtrack.features.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}