package com.notification.eventdriven.service;

import com.notification.eventdriven.model.Notification;
import lombok.extern.slf4j.Slf4j;

public interface NotificationSender {
    void send(Notification notification) throws Exception;
}
