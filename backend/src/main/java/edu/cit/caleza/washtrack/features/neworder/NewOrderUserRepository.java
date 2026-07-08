package edu.cit.caleza.washtrack.features.neworder;

import edu.cit.caleza.washtrack.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewOrderUserRepository extends JpaRepository<User, Long> {
}