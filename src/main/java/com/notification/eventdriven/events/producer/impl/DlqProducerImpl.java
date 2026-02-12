package com.notification.eventdriven.events.producer.impl;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.RetryHeaders;
import com.notification.eventdriven.events.producer.DlqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;

@Slf4j
@Component
public class DlqProducerImpl implements DlqProducer {

    private static final String DLQ_TOPIC = "notification.dlq";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public DlqProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(NotificationEvent event, Exception cause) {

        log.error(
                "Sending event {} to DLQ after retries exhausted",
                event.getEventId()
        );

        kafkaTemplate.send(
                MessageBuilder
                        .withPayload(event)
                        .setHeader(KafkaHeaders.TOPIC, DLQ_TOPIC) // ⭐ THIS LINE
                        .setHeader(RetryHeaders.ERROR_REASON, cause.getMessage())
                        .build()
        );
    }
}
