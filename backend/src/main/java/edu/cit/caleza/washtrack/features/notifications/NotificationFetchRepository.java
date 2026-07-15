package edu.cit.caleza.washtrack.features.notifications;

import edu.cit.caleza.washtrack.shared.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationFetchRepository extends JpaRepository<Notification, Long> {
    // FR-010: a user's own notifications, newest first
    List<Notification> findByUser_UserIdOrderBySentAtDesc(Long userId);

    List<Notification> findByUser_UserIdAndIsReadFalse(Long userId);
}
