package edu.cit.caleza.washtrack.repository;

import edu.cit.caleza.washtrack.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderBySentAtDesc(Long userId);
}
