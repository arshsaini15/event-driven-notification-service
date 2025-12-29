package com.notification.eventdriven.service.impl;

import com.notification.eventdriven.enums.NotificationStatus;
import com.notification.eventdriven.exceptions.NotificationNotFoundException;
import com.notification.eventdriven.model.Notification;
import com.notification.eventdriven.repository.NotificationRepository;
import com.notification.eventdriven.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class NotificationServiceImpl implements NotificationService {

    private static NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification createIfNotExists(Long eventId, String message) {
        try {
            return notificationRepository.save(
                    new Notification(eventId, message)
            );
        } catch (DataIntegrityViolationException ex) {
            // Another thread / instance already inserted it
            return notificationRepository.findByEventId(eventId)
                    .orElseThrow();
        }
    }

    @Override
    public Optional<Notification> getByEventId(Long eventId) {
        return notificationRepository.findByEventId(eventId);
    }

    @Override
    @Transactional
    public Notification updateStatus(Long notificationId, NotificationStatus status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with id " + notificationId + " not found"
                        )
                );
        notification.updateStatus(status);
        return notification;
    }

    @Override
    public Page<Notification> getNotifications(NotificationStatus status, Pageable pageable) {

        if (status == null) {
            return notificationRepository.findAll(pageable);
        }

        return notificationRepository.findByStatus(status, pageable);
    }
}
