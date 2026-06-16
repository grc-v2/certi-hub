package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.application.command.TrackerDataCommandService;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tracker/data")
public class TrackerDataController {

    private final TrackerDataCommandService trackerDataCommandService;

    public TrackerDataController(TrackerDataCommandService trackerDataCommandService) {
        this.trackerDataCommandService = trackerDataCommandService;
    }

    /** Reset the current user's tracker content (tasks, resources, collections). */
    @DeleteMapping
    public ResponseEntity<Void> reset() {
        Long userId = SecurityUtils.getCurrentUserId();
        trackerDataCommandService.resetForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
