package edu.cit.caleza.washtrack.features.notifications;

import edu.cit.caleza.washtrack.shared.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationFetchRepository notificationRepository;

    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser_UserIdOrderBySentAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUser_UserIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .notedId(notification.getNotedId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .sentAt(notification.getSentAt())
                .build();
    }
}
