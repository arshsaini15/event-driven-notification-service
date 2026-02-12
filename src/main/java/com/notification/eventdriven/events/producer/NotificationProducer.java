package com.notification.eventdriven.events.producer;

import com.notification.eventdriven.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationProducer {

    private static final String MAIN_TOPIC = "notification.main";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(NotificationEvent event){

        log.info("Publishing event {}", event.getEventId());

        kafkaTemplate.send(
                MAIN_TOPIC,
                event.getEventId(),
                event
        );
    }
}
