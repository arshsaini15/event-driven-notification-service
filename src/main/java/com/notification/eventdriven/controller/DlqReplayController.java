package com.notification.eventdriven.controller;

import com.notification.eventdriven.service.DlqReplayService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dlq")
public class DlqReplayController {

    private final DlqReplayService replayService;

    public DlqReplayController(DlqReplayService replayService) {
        this.replayService = replayService;
    }

    @PostMapping("/replay/{eventId}")
    public ResponseEntity<String> replay(@PathVariable String eventId) {

        replayService.replay(eventId);

        return ResponseEntity.ok(
                "Replay triggered for eventId=" + eventId
        );
    }
}
