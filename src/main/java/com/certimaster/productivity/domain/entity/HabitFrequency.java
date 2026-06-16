package com.certimaster.productivity.domain.entity;

/** How often a {@link Habit} is expected to be performed. */
public enum HabitFrequency {
    /** Every day. */
    DAILY,
    /** Only on the configured weekdays. */
    WEEKLY
}
