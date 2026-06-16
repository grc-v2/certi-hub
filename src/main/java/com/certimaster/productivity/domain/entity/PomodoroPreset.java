package com.certimaster.productivity.domain.entity;

/** Fixed focus/break presets for the Pomodoro timer. */
public enum PomodoroPreset {
    CLASSIC(25, 5),
    DEEP(50, 10);

    private final int focusMinutes;
    private final int breakMinutes;

    PomodoroPreset(int focusMinutes, int breakMinutes) {
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
    }

    public int getFocusMinutes() {
        return focusMinutes;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }
}
