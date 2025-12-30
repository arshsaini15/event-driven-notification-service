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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification createIfNotExists(Long eventId, String message) {

        Optional<Notification> existing = notificationRepository.findByEventId(eventId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Notification notification = new Notification(eventId, message);
        return notificationRepository.save(notification);
    }


    @Override
    public Notification getByEventId(Long eventId) {
        return notificationRepository.findByEventId(eventId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with eventId " + eventId + " not found"
                        )
                );
    }

    @Override
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
        return (status == null)
                ? notificationRepository.findAll(pageable)
                : notificationRepository.findByStatus(status, pageable);
    }
}
