package com.certimaster.productivity.domain.entity;

/** What a recorded {@link PomodoroSession} was counting down: focus work or a break. */
public enum PomodoroSessionKind {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
}
