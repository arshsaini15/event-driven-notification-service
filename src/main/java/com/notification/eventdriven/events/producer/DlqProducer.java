package com.notification.eventdriven.events.producer;

import com.notification.eventdriven.events.NotificationEvent;

public interface DlqProducer {
    void publish(NotificationEvent event, Exception cause);
}
