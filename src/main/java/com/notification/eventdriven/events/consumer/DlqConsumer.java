package com.notification.eventdriven.events.consumer;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.RetryHeaders;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DlqConsumer {

    @KafkaListener(
            topics = "notification.dlq",
            groupId = "notification-dlq-monitor"
    )

    public void consume(
            NotificationEvent event,
            @Header(name = RetryHeaders.ERROR_REASON, required = false) String errorReason,
            Acknowledgment ack
    ) {

        log.error(
                "DLQ MESSAGE RECEIVED → eventId={}, reason={}",
                event.getEventId(),
                errorReason
        );

        // NO AUTO-REPLAY
        ack.acknowledge();
    }
}
