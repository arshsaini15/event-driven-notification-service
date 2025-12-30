package com.notification.eventdriven.events;

import com.notification.eventdriven.enums.NotificationEventType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class NotificationEvent {
    @NotNull
    private String eventId;

    private NotificationEvent eventType;
    private String message;

    private Instant occuredAt;

    public String getEventId() {
        return eventId;
    }

    public NotificationEventType getEventType() {return eventType.getEventType();}

    public String getMessage() {return message;}


}
