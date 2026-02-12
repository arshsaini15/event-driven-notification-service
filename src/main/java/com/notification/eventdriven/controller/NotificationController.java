package com.notification.eventdriven.controller;

import com.notification.eventdriven.dto.response.NotificationResponse;
import com.notification.eventdriven.mapper.NotificationMapper;
import com.notification.eventdriven.service.NotificationService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/event/{eventId}")
    public NotificationResponse getByEventId(@PathVariable String eventId) {
        return NotificationMapper.toDto(
                notificationService.getByEventId(eventId)
        );
    }

    @PostMapping("/_test/create")
    public NotificationResponse testCreate(
            @RequestParam String eventId,
            @RequestParam String message
    ) {
        return NotificationMapper.toDto(
                notificationService.createIfNotExists(eventId, message)
        );
    }
}
