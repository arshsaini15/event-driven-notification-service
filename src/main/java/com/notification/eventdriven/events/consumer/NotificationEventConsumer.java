package com.notification.eventdriven.events.consumer;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    public NotificationEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "notification-events",
            groupId = "notification-consumer"
    )
    public void consume(NotificationEvent event) {

        if (event.getEventId() == null) {
            throw new IllegalArgumentException("eventId cannot be null");
        }

        notificationService.createIfNotExists(
                event.getEventId(),
                event.getMessage()
        );
    }
}
