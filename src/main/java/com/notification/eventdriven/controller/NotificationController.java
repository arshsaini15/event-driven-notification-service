package com.notification.eventdriven.controller;

import com.notification.eventdriven.dto.request.UpdateStatusRequest;
import com.notification.eventdriven.dto.response.NotificationResponse;
import com.notification.eventdriven.enums.NotificationStatus;
import com.notification.eventdriven.exceptions.NotificationNotFoundException;
import com.notification.eventdriven.mapper.NotificationMapper;
import com.notification.eventdriven.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/event/{eventId}")
    public NotificationResponse getByEventId(@PathVariable Long eventId) {
        return NotificationMapper.toDto(
                notificationService.getByEventId(eventId)
        );
    }

    @PutMapping("/{notificationId}/status")
    public NotificationResponse updateStatus(
            @PathVariable Long notificationId,
            @RequestBody @Valid UpdateStatusRequest request
    ) {
        return NotificationMapper.toDto(
                notificationService.updateStatus(notificationId, request.getStatus())
        );
    }

    @GetMapping
    public Page<NotificationResponse> getNotifications(
            @RequestParam(required = false) NotificationStatus status,
            Pageable pageable
    ) {
        return notificationService.getNotifications(status, pageable)
                .map(NotificationMapper::toDto);
    }

}
