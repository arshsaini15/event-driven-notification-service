package com.notification.eventdriven.events.producer.impl;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.producer.DlqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
                "Publishing event {} to DLQ [{}] due to {}",
                event.getEventId(),
                DLQ_TOPIC,
                cause.getMessage()
        );

        kafkaTemplate.send(DLQ_TOPIC, event.getEventId(), event);
    }
}
