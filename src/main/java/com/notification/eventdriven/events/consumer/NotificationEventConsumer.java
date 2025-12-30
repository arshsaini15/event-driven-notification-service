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
            groupId = "notification-service"
    )

    public void consume(NotificationEvent event) {

        log.info("Received notification event {}", event.getEventId());

        if (event.getEventId() == null) {
            log.error("Received event without eventId: {}", event);
            return;
        }

        notificationService.createIfNotExists(
                event.getEventId(),
                event.getMessage()
        );
    }
}
