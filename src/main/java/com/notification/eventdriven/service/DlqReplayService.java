package com.notification.eventdriven.service;

import com.notification.eventdriven.enums.NotificationEventType;
import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.producer.DlqReplayProducer;
import com.notification.eventdriven.model.Notification;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DlqReplayService {

    private final NotificationService notificationService;
    private final DlqReplayProducer replayProducer;

    public DlqReplayService(
            NotificationService notificationService,
            DlqReplayProducer replayProducer
    ) {
        this.notificationService = notificationService;
        this.replayProducer = replayProducer;
    }

    public void replay(String eventId) {

        Notification notification =
                notificationService.getByEventId(eventId);

        if (notification.getStatus().name().equals("SENT")) {
            log.info(
                    "Notification already SENT. Replay skipped → eventId={}",
                    eventId
            );
            return;
        }

        if (!notification.getStatus().name().equals("DEAD")) {
            throw new IllegalStateException(
                    "Only DEAD notifications can be replayed"
            );
        }

        NotificationEvent event = new NotificationEvent(
                eventId,
                NotificationEventType.REPLAY,
                notification.getMessage()
        );


        replayProducer.replay(event);
    }
}
