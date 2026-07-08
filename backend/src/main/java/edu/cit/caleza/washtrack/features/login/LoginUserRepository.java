package edu.cit.caleza.washtrack.features.login;

import edu.cit.caleza.washtrack.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoginUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}