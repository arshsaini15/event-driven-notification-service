package com.notification.eventdriven.dto.response;

import com.notification.eventdriven.enums.NotificationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long eventId;
    private NotificationStatus status;
    private int retryCount;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;

    public NotificationResponse(
            Long id,
            Long eventId,
            NotificationStatus status,
            int retryCount,
            String message,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.eventId = eventId;
        this.status = status;
        this.retryCount = retryCount;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
