package com.certimaster.engagement.adapter.web;

import com.certimaster.engagement.adapter.web.request.BroadcastNotificationRequest;
import com.certimaster.engagement.application.command.NotificationCommandService;
import com.certimaster.shared.event.BroadcastNotificationRequestedEvent;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    private final NotificationCommandService notificationCommandService;

    public AdminNotificationController(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    /**
     * Queue an in-app broadcast. Recipients are resolved and notifications are created
     * asynchronously, so this returns 202 Accepted as soon as the request is accepted.
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcast(@Valid @RequestBody BroadcastNotificationRequest request) {
        notificationCommandService.requestBroadcast(new BroadcastNotificationRequestedEvent(
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getTargetRole()));
        return ResponseEntity.accepted().build();
    }
}
