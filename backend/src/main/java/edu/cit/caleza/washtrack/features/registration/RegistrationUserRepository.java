package edu.cit.caleza.washtrack.features.registration;

import edu.cit.caleza.washtrack.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}