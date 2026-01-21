package com.notification.eventdriven.events;

import com.notification.eventdriven.enums.NotificationEventType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class NotificationEvent {

    @NotNull
    private String eventId;

    @NotNull
    private NotificationEventType eventType;

    @NotNull
    private String message;

    @NotNull
    private Instant occurredAt;

    // REQUIRED for Kafka deserialization
    public NotificationEvent() {
    }

    public NotificationEvent(
            String eventId,
            NotificationEventType eventType,
            String message
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.message = message;
        this.occurredAt = Instant.now();
    }

    public String getEventId() {
        return eventId;
    }

    public NotificationEventType getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
