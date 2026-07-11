package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.adapter.web.request.RecordPomodoroSessionRequest;
import com.certimaster.productivity.adapter.web.response.PomodoroSessionResponse;
import com.certimaster.productivity.adapter.web.response.PomodoroStatsResponse;
import com.certimaster.productivity.application.command.PomodoroSessionCommandService;
import com.certimaster.productivity.application.command.RecordPomodoroSessionCommand;
import com.certimaster.productivity.application.query.GetPomodoroStatsQuery;
import com.certimaster.productivity.application.query.ListPomodoroSessionsQuery;
import com.certimaster.productivity.application.query.PomodoroSessionQueryService;
import com.certimaster.productivity.domain.entity.PomodoroSessionKind;
import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * History and stats for client-side Pomodoro runs. Sibling of
 * {@link PomodoroSettingController}: settings say how long the timer runs,
 * sessions record what actually happened.
 */
@RestController
@RequestMapping("/api/v1/pomodoro/sessions")
public class PomodoroSessionController {

    private final PomodoroSessionCommandService sessionCommandService;
    private final PomodoroSessionQueryService sessionQueryService;

    public PomodoroSessionController(PomodoroSessionCommandService sessionCommandService,
                                     PomodoroSessionQueryService sessionQueryService) {
        this.sessionCommandService = sessionCommandService;
        this.sessionQueryService = sessionQueryService;
    }

    @PostMapping
    public ResponseEntity<PomodoroSessionResponse> record(@RequestBody RecordPomodoroSessionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        PomodoroSessionResponse recorded = sessionCommandService.record(new RecordPomodoroSessionCommand(
                userId,
                request.getTaskId(),
                parseKind(request.getKind()),
                parseDateTime(request.getStartedAt()),
                request.getDurationSeconds() != null ? request.getDurationSeconds() : 0,
                request.getCompleted() == null || request.getCompleted()));
        return ResponseEntity.status(HttpStatus.CREATED).body(recorded);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PomodoroSessionResponse>> history(
            @RequestParam(required = false) Long taskId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(sessionQueryService.history(
                new ListPomodoroSessionsQuery(userId, taskId, page, size)));
    }

    @GetMapping("/stats")
    public ResponseEntity<PomodoroStatsResponse> stats(@RequestParam(required = false) Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(sessionQueryService.stats(new GetPomodoroStatsQuery(userId, taskId)));
    }

    private static PomodoroSessionKind parseKind(String raw) {
        if (raw == null || raw.isBlank()) {
            return null; // command service rejects with a clear message
        }
        try {
            return PomodoroSessionKind.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw BusinessException.invalidInput(
                    "Unknown session kind: " + raw + " (expected FOCUS, SHORT_BREAK or LONG_BREAK)");
        }
    }

    private static LocalDateTime parseDateTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null; // command service rejects with a clear message
        }
        try {
            return LocalDateTime.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw BusinessException.invalidInput(
                    "startedAt must be an ISO local datetime, e.g. 2026-07-11T09:30:00");
        }
    }
}
