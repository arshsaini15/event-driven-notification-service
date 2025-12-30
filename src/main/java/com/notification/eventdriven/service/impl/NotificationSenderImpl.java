package com.notification.eventdriven.service.impl;

import com.notification.eventdriven.model.Notification;
import com.notification.eventdriven.service.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationSenderImpl implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info(
                "Sending notification [id={}, eventId={}, message={}]",
                notification.getId(),
                notification.getEventId(),
                notification.getMessage()
        );
    }
}
