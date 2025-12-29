package com.notification.eventdriven.mapper;

import com.notification.eventdriven.dto.response.NotificationResponse;
import com.notification.eventdriven.model.Notification;

public class NotificationMapper {

    private NotificationMapper() {}

    public static NotificationResponse toDto(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getEventId(),
                notification.getStatus(),
                notification.getRetryCount(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
