package com.notification.eventdriven.service;

import com.notification.eventdriven.model.Notification;

public interface NotificationSender {
    void send(Notification notification) throws Exception;
}
