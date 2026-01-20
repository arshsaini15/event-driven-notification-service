package com.notification.eventdriven.events.consumer;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.RetryHeaders;
import com.notification.eventdriven.events.producer.DlqProducer;
import com.notification.eventdriven.events.producer.RetryProducer;
import com.notification.eventdriven.events.retry.RetryPolicy;
import com.notification.eventdriven.exceptions.PermanentNotificationException;
import com.notification.eventdriven.exceptions.TransientNotificationException;

import com.notification.eventdriven.service.NotificationService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

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

    @KafkaListener(
            topics = {
                    "notification.main",
                    "notification.retry.5s",
                    "notification.retry.30s",
                    "notification.retry.5m"
            },
            groupId = "notification-service"
    )
    public void consume(
            NotificationEvent event,
            @Header(name = RetryHeaders.RETRY_COUNT, required = false) Integer retryCount,
            Acknowledgment ack
    ) {

        int currentRetry = (retryCount == null) ? 0 : retryCount;

        try {
            validate(event);

            notificationService.createIfNotExists(
                    event.getEventId(),
                    event.getMessage()
            );

            // success → commit offset
            ack.acknowledge();

        } catch (PermanentNotificationException e) {

            log.error(
                    "Permanent failure for event {}. Sending to DLQ",
                    safeEventId(event),
                    e
            );

            dlqProducer.publish(event, e);
            ack.acknowledge();

        } catch (TransientNotificationException e) {

            handleTransientFailure(event, currentRetry, e);
            ack.acknowledge();

        } catch (Exception e) {

            log.error(
                    "Unknown exception for event {}. Treating as transient",
                    safeEventId(event),
                    e
            );

            handleTransientFailure(event, currentRetry, e);
            ack.acknowledge();
        }
    }

    /* -------------------- helpers -------------------- */

    private void handleTransientFailure(
            NotificationEvent event,
            int currentRetry,
            Exception e
    ) {
        if (currentRetry < RetryPolicy.MAX_RETRIES) {

            log.warn(
                    "Transient failure for event {}. Retrying attempt {}",
                    safeEventId(event),
                    currentRetry + 1
            );

            retryProducer.publish(event, currentRetry, e);

        } else {

            log.error(
                    "Retries exhausted for event {}. Sending to DLQ",
                    safeEventId(event)
            );

            dlqProducer.publish(event, e);
        }
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

    private String safeEventId(NotificationEvent event) {
        return event == null ? "null" : event.getEventId();
    }
}
