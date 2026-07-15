package edu.cit.caleza.washtrack.features.neworder;

import edu.cit.caleza.washtrack.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewOrderUserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(User.Role role);
}