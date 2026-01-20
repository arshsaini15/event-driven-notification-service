package com.notification.eventdriven.events.producer;

import com.notification.eventdriven.events.NotificationEvent;

public interface RetryProducer {

    void publish(
            NotificationEvent event,
            int currentRetry,
            Exception cause
    );
}
