package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.HabitFrequency;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record CreateHabitCommand(
        Long userId,
        String name,
        String color,
        HabitFrequency frequency,
        Set<DayOfWeek> weekdays,
        LocalTime reminderTime
) {
}
