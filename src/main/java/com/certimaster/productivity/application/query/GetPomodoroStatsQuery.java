package com.certimaster.productivity.application.query;

/** Focus stats for today + the last 7 days; {@code taskId} optionally adds a per-task count. */
public record GetPomodoroStatsQuery(Long userId, Long taskId) {
}
