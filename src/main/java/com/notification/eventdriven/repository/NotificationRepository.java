package com.notification.eventdriven.repository;

import com.notification.eventdriven.enums.NotificationStatus;
import com.notification.eventdriven.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByEventId(String eventId);

    List<Notification> findByStatus(NotificationStatus status);
}
