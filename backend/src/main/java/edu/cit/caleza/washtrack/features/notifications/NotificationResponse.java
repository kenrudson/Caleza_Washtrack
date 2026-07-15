package edu.cit.caleza.washtrack.features.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private Long notedId;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime sentAt;
}
