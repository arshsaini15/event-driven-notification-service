package com.notification.eventdriven.scheduler;

import com.notification.eventdriven.model.Notification;
import com.notification.eventdriven.service.NotificationSender;
import com.notification.eventdriven.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NotificationRetryJob {
    private final NotificationService notificationService;
    private final NotificationSender notificationSender;

    public NotificationRetryJob(NotificationService notificationService, NotificationSender notificationSender) {
        this.notificationService = notificationService;
        this.notificationSender = notificationSender;
    }

    @Scheduled(fixedDelay = 15000)
    public void processPendingNotifications() {
        List<Notification> pending =
                notificationService.getPendingNotifications();

        for (Notification notification : pending) {
            try {
                notificationSender.send(notification);
                notificationService.markSent(notification.getId());
            } catch (Exception ex) {
                log.error(
                        "Failed to send notification id={}",
                        notification.getId(),
                        ex
                );
                notificationService.markFailed(notification.getId());
            }
        }
    }
}
