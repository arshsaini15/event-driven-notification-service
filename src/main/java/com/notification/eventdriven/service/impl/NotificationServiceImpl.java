package com.notification.eventdriven.service.impl;

import com.notification.eventdriven.exceptions.NotificationNotFoundException;
import com.notification.eventdriven.model.Notification;
import com.notification.eventdriven.repository.NotificationRepository;
import com.notification.eventdriven.service.NotificationService;

import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Idempotent create.
     * Safe under concurrent Kafka consumers.
     */
    @Override
    @Transactional
    public Notification createIfNotExists(String eventId, String message) {

        try {
            Notification notification = new Notification(eventId, message);
            return notificationRepository.save(notification);

        } catch (DataIntegrityViolationException ex) {
            // another consumer already created it
            return notificationRepository.findByEventId(eventId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Duplicate eventId but notification not found: " + eventId
                            )
                    );
        }
    }

    @Override
    public Notification getByEventId(String eventId) {
        return notificationRepository.findByEventId(eventId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with eventId " + eventId + " not found"
                        )
                );
    }

    /**
     * Called by Kafka consumer when processing starts.
     * This acts as a logical lock.
     */
    @Override
    @Transactional
    public void markProcessing(Long notificationId) {
        Notification notification = getById(notificationId);
        notification.markProcessing();
    }

    /**
     * Called after successful delivery.
     */
    @Override
    @Transactional
    public void markSent(Long notificationId) {
        Notification notification = getById(notificationId);
        notification.markSent();
    }

    /**
     * Called when a transient failure happens and retry is needed.
     */
    @Override
    @Transactional
    public void markRetry(
            Long notificationId,
            Instant nextRetryAt
    ) {
        Notification notification = getById(notificationId);
        notification.markRetry(
                com.notification.eventdriven.enums.FailureType.TRANSIENT,
                nextRetryAt
        );
    }

    /**
     * Called when retries are exhausted or failure is permanent.
     */
    @Override
    @Transactional
    public void markDead(Long notificationId) {
        Notification notification = getById(notificationId);
        notification.markDead(
                com.notification.eventdriven.enums.FailureType.PERMANENT
        );
    }

    private Notification getById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with id " + notificationId + " not found"
                        )
                );
    }
}
