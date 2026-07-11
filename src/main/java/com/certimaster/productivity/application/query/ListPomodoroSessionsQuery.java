package com.certimaster.productivity.application.query;

/** Paged session history, newest first; {@code taskId} optionally narrows to one todo task. */
public record ListPomodoroSessionsQuery(Long userId, Long taskId, int page, int size) {
}
