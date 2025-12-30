package com.notification.eventdriven.service;

import com.notification.eventdriven.model.Notification;
import com.notification.eventdriven.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification createIfNotExists(String eventId, String message);

    Notification getByEventId(String eventId);

    Notification updateStatus(Long notificationId, NotificationStatus status);

    Page<Notification> getNotifications(
            NotificationStatus status,
            Pageable pageable
    );

    List<Notification> getPendingNotifications();

    void markSent(Long notificationId);

    void markFailed(Long notificationId);
}
