package com.notification.eventdriven.service.impl;

import com.notification.eventdriven.enums.NotificationStatus;
import com.notification.eventdriven.exceptions.NotificationNotFoundException;
import com.notification.eventdriven.model.Notification;
import com.notification.eventdriven.repository.NotificationRepository;
import com.notification.eventdriven.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRIES = 3;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification createIfNotExists(String eventId, String message) {

        if (notificationRepository.existsByEventId(eventId)) {
            return notificationRepository.findByEventId(eventId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "eventId exists but notification not found: " + eventId
                            )
                    );
        }

        try {
            Notification notification = new Notification(eventId, message);
            return notificationRepository.save(notification);
        } catch (DataIntegrityViolationException ex) {
            // another concurrent consumer inserted the same eventId
            return notificationRepository.findByEventId(eventId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Duplicate eventId but notification not found: " + eventId
                            )
                    );
        }
    }

    @Override
    public Notification getByEventId(String eventId) {
        return notificationRepository.findByEventId(eventId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with eventId " + eventId + " not found"
                        )
                );
    }

    @Override
    @Transactional
    public Notification updateStatus(Long notificationId, NotificationStatus status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with id " + notificationId + " not found"
                        )
                );
        notification.updateStatus(status);
        return notification;
    }

    @Override
    public Page<Notification> getNotifications(NotificationStatus status, Pageable pageable) {
        return (status == null)
                ? notificationRepository.findAll(pageable)
                : notificationRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Notification> getPendingNotifications() {

        Pageable pageable = PageRequest.of(0, BATCH_SIZE);

        Page<Notification> createdPage =
                notificationRepository.findByStatus(NotificationStatus.CREATED, pageable);

        Page<Notification> failedPage =
                notificationRepository.findByStatus(NotificationStatus.FAILED, pageable);

        List<Notification> pending = new ArrayList<>();

        pending.addAll(createdPage.getContent());

        for (Notification notification : failedPage.getContent()) {
            if (notification.getRetryCount() < MAX_RETRIES) {
                pending.add(notification);
            }
        }

        return pending;
    }


    @Override
    @Transactional
    public void markSent(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with id " + notificationId + " not found"
                        )
                );

        if (notification.getStatus() == NotificationStatus.SENT) {
            return;
        }

        notification.mark_Sent();
    }


    @Override
    @Transactional
    public void markFailed(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification with id " + notificationId + " not found"
                        )
                );

        if (notification.getStatus() == NotificationStatus.SENT) {
            return;
        }

        notification.incrementRetry();
        notification.mark_Failed();
    }

}
