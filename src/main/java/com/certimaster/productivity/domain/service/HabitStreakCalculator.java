package com.certimaster.productivity.domain.service;

import com.certimaster.productivity.domain.entity.Habit;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

/**
 * Computes a habit's current streak: the number of consecutive <em>due</em> days,
 * counting back from today, that have a check-in. Non-due days are skipped (they
 * neither add to nor break the streak). Today is forgiving — if today is due but
 * not yet done, the streak is measured from yesterday rather than broken.
 */
@Component
public class HabitStreakCalculator {

    /** Hard bound so a habit with no due days (e.g. WEEKLY with no weekdays) can't loop forever. */
    private static final int MAX_LOOKBACK_DAYS = 366;

    public int currentStreak(Habit habit, Set<LocalDate> checkins, LocalDate today) {
        LocalDate cursor = today;

        // A due-but-not-yet-done today doesn't break the streak; start measuring from yesterday.
        if (habit.isDueOn(today) && !checkins.contains(today)) {
            cursor = today.minusDays(1);
        }

        int streak = 0;
        LocalDate floor = today.minusDays(MAX_LOOKBACK_DAYS);
        while (!cursor.isBefore(floor)) {
            if (habit.isDueOn(cursor)) {
                if (checkins.contains(cursor)) {
                    streak++;
                    cursor = cursor.minusDays(1);
                } else {
                    break;
                }
            } else {
                cursor = cursor.minusDays(1);
            }
        }
        return streak;
    }
}
