package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.HabitFrequency;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

/**
 * Partial update. Null scalar fields are left unchanged. {@code clearReminder} forces
 * the reminder time to null (distinguishing "no change" from "remove the reminder").
 */
public record UpdateHabitCommand(
        Long id,
        Long userId,
        String name,
        String color,
        HabitFrequency frequency,
        Set<DayOfWeek> weekdays,
        LocalTime reminderTime,
        boolean clearReminder,
        Boolean active,
        Integer orderIndex
) {
}
