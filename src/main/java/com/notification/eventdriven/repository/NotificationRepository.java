package com.notification.eventdriven.repository;

import com.notification.eventdriven.enums.NotificationStatus;
import com.notification.eventdriven.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByEventId(Long eventId);

    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);
}
