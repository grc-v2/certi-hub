package com.certimaster.productivity.application.command;

import java.time.LocalDate;

/** Confirm ({@code done = true}) or un-confirm a habit for a given day. */
public record SetHabitCheckinCommand(Long habitId, Long userId, LocalDate date, boolean done) {
}
