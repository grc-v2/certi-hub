package com.certimaster.productivity.application.query;

import java.time.LocalDate;

public record GetHabitHistoryQuery(Long habitId, Long userId, LocalDate from, LocalDate to) {
}
