package com.notification.eventdriven.controller;

import com.notification.eventdriven.enums.NotificationEventType;
import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.producer.NotificationProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final NotificationProducer producer;

    public EventController(NotificationProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/publish")
    public void publish(
            @RequestParam String eventId,
            @RequestParam String message
    ) {

        NotificationEvent event =
                new NotificationEvent(
                        eventId,
                        NotificationEventType.USER_EVENT,
                        message
                );

        producer.publish(event);
    }
}
