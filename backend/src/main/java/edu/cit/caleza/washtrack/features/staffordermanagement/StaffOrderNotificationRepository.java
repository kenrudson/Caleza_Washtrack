package edu.cit.caleza.washtrack.features.staffordermanagement;

import edu.cit.caleza.washtrack.shared.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

// Slice-owned: this slice only ever needs to create a notification when a
// status changes (BR-005).
public interface StaffOrderNotificationRepository extends JpaRepository<Notification, Long> {
}
