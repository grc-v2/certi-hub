package com.certimaster.productivity.domain.service;

import com.certimaster.productivity.domain.entity.Habit;
import com.certimaster.productivity.domain.entity.HabitFrequency;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class HabitStreakCalculatorTest {

    private final HabitStreakCalculator calculator = new HabitStreakCalculator();

    // 2026-07-10 is a Friday.
    private static final LocalDate TODAY = LocalDate.of(2026, 7, 10);

    private Habit daily() {
        return Habit.builder().userId(1L).name("Gym").frequency(HabitFrequency.DAILY).build();
    }

    private Habit weekly(DayOfWeek... days) {
        return Habit.builder().userId(1L).name("Review").frequency(HabitFrequency.WEEKLY)
                .weekdays(Set.of(days)).build();
    }

    @Test
    void dailyHabitCountsConsecutiveCheckedDaysIncludingToday() {
        Set<LocalDate> checkins = Set.of(TODAY, TODAY.minusDays(1), TODAY.minusDays(2));

        assertThat(calculator.currentStreak(daily(), checkins, TODAY)).isEqualTo(3);
    }

    @Test
    void dueTodayButNotYetDoneDoesNotBreakStreak() {
        Set<LocalDate> checkins = Set.of(TODAY.minusDays(1), TODAY.minusDays(2));

        assertThat(calculator.currentStreak(daily(), checkins, TODAY)).isEqualTo(2);
    }

    @Test
    void missedDueDayBreaksStreak() {
        // Yesterday was due and missed: only today counts.
        Set<LocalDate> checkins = Set.of(TODAY, TODAY.minusDays(2), TODAY.minusDays(3));

        assertThat(calculator.currentStreak(daily(), checkins, TODAY)).isEqualTo(1);
    }

    @Test
    void noCheckinsMeansZeroStreak() {
        assertThat(calculator.currentStreak(daily(), Set.of(), TODAY)).isZero();
    }

    @Test
    void weeklyHabitSkipsNonDueDays() {
        // Due Mondays and Thursdays. Today (Fri) is not due; Thu 07-09 and Mon 07-06
        // are checked, Thu 07-02 is not — streak of 2.
        Habit habit = weekly(DayOfWeek.MONDAY, DayOfWeek.THURSDAY);
        Set<LocalDate> checkins = Set.of(LocalDate.of(2026, 7, 9), LocalDate.of(2026, 7, 6));

        assertThat(calculator.currentStreak(habit, checkins, TODAY)).isEqualTo(2);
    }

    @Test
    void inactiveHabitHasZeroStreak() {
        Habit habit = daily();
        habit.setActive(false);
        Set<LocalDate> checkins = Set.of(TODAY, TODAY.minusDays(1));

        assertThat(calculator.currentStreak(habit, checkins, TODAY)).isZero();
    }

    @Test
    void weeklyHabitWithNoWeekdaysTerminatesWithZero() {
        // No due days at all — the lookback bound must stop the walk.
        Habit habit = Habit.builder().userId(1L).name("Never due")
                .frequency(HabitFrequency.WEEKLY).build();

        assertThat(calculator.currentStreak(habit, Set.of(TODAY), TODAY)).isZero();
    }
}
