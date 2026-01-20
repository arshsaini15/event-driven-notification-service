package com.notification.eventdriven.events.producer.impl;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.producer.RetryProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryProducerImpl implements RetryProducer {

    private static final String RETRY_TOPIC = "notification.retry.5s";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public RetryProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(NotificationEvent event, Exception cause) {
        log.warn(
                "Publishing event {} to RETRY topic [{}] due to {}",
                event.getEventId(),
                RETRY_TOPIC,
                cause.getMessage()
        );

        kafkaTemplate.send(RETRY_TOPIC, event.getEventId(), event);
    }
}
