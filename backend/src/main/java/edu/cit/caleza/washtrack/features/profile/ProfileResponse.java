package edu.cit.caleza.washtrack.features.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;
    private LocalDateTime createdAt;
}
