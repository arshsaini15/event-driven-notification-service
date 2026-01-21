package com.notification.eventdriven.events.producer;

import com.notification.eventdriven.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DlqReplayProducer {

    private static final String MAIN_TOPIC = "notification.main";

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public DlqReplayProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void replay(NotificationEvent event) {

        log.info(
                "Replaying DLQ event back to main topic → eventId={}",
                event.getEventId()
        );

        kafkaTemplate.send(
                MAIN_TOPIC,
                event.getEventId(),
                event
        );
    }
}
