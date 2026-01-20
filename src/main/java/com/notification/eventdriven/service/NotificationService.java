package com.notification.eventdriven.service;

import com.notification.eventdriven.model.Notification;

import java.time.Instant;

public interface NotificationService {

    /**
     * Idempotent create.
     * Safe under concurrent Kafka consumers.
     */
    Notification createIfNotExists(String eventId, String message);

    /**
     * Fetch notification by upstream event id.
     */
    Notification getByEventId(String eventId);

    /**
     * Marks notification as being processed.
     * Acts as a logical lock.
     */
    void markProcessing(Long notificationId);

    /**
     * Marks notification as successfully delivered.
     */
    void markSent(Long notificationId);

    /**
     * Marks notification for retry with calculated backoff.
     */
    void markRetry(Long notificationId, Instant nextRetryAt);

    /**
     * Marks notification as permanently failed (DLQ).
     */
    void markDead(Long notificationId);
}
