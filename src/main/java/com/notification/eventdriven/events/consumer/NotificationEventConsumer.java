package com.notification.eventdriven.events.consumer;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.producer.DlqProducer;
import com.notification.eventdriven.events.producer.RetryProducer;
import com.notification.eventdriven.exceptions.PermanentNotificationException;
import com.notification.eventdriven.exceptions.TransientNotificationException;
import com.notification.eventdriven.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final RetryProducer retryProducer;
    private final DlqProducer dlqProducer;

    public NotificationEventConsumer(
            NotificationService notificationService,
            RetryProducer retryProducer,
            DlqProducer dlqProducer
    ) {
        this.notificationService = notificationService;
        this.retryProducer = retryProducer;
        this.dlqProducer = dlqProducer;
    }

    private void validate(NotificationEvent event) {
        if (event == null) {
            throw new PermanentNotificationException("Event is null");
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new PermanentNotificationException("Missing eventId");
        }

        if (event.getMessage() == null || event.getMessage().isBlank()) {
            throw new PermanentNotificationException("Missing message");
        }
    }

    @KafkaListener(
            topics = "notification-events",
            groupId = "notification-service"
    )
    public void consume(
            NotificationEvent event,
            Acknowledgment ack
    ) {
        try {
            validate(event);

            notificationService.createIfNotExists(
                    event.getEventId(),
                    event.getMessage()
            );

            // success path
            ack.acknowledge();

        } catch (PermanentNotificationException e) {
            dlqProducer.publish(event, e);
            ack.acknowledge();

        } catch (TransientNotificationException e) {
            retryProducer.publish(event, e);
            ack.acknowledge();

        } catch (Exception e) {
            retryProducer.publish(event, e);
            ack.acknowledge();
        }
    }
}
