package edu.cit.caleza.washtrack.features.neworder;

import edu.cit.caleza.washtrack.shared.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewOrderNotificationRepository extends JpaRepository<Notification, Long> {
}