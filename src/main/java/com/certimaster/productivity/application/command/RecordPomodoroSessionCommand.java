package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.PomodoroSessionKind;

import java.time.LocalDateTime;

/**
 * Record one finished/abandoned client-side Pomodoro run. {@code taskId} is optional;
 * when present it must reference a todo task owned by {@code userId}.
 */
public record RecordPomodoroSessionCommand(
        Long userId,
        Long taskId,
        PomodoroSessionKind kind,
        LocalDateTime startedAt,
        int durationSeconds,
        boolean completed) {
}
