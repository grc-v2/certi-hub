package com.certimaster.productivity.adapter.scheduler;

import com.certimaster.productivity.adapter.persistence.HabitCheckinRepository;
import com.certimaster.productivity.adapter.persistence.HabitRepository;
import com.certimaster.productivity.domain.entity.Habit;
import com.certimaster.productivity.domain.event.HabitReminderDueEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Fires habit reminders. Runs every minute and, for each active habit with a reminder
 * that is due today, has reached its reminder time, has not been checked in, and has
 * not already been reminded today, publishes a {@link HabitReminderDueEvent} and stamps
 * {@code lastRemindedOn}. The "on/after time, once per day" rule makes it robust to
 * downtime — a missed exact minute still reminds later that day.
 */
@Component
public class HabitReminderScheduler {

    private final HabitRepository habitRepository;
    private final HabitCheckinRepository checkinRepository;
    private final ApplicationEventPublisher events;

    public HabitReminderScheduler(HabitRepository habitRepository,
                                  HabitCheckinRepository checkinRepository,
                                  ApplicationEventPublisher events) {
        this.habitRepository = habitRepository;
        this.checkinRepository = checkinRepository;
        this.events = events;
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    @Transactional
    public void fireDueReminders() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Habit> candidates = habitRepository.findByActiveTrueAndReminderTimeIsNotNull();
        for (Habit habit : candidates) {
            if (today.equals(habit.getLastRemindedOn())) {
                continue; // already reminded today
            }
            if (!habit.isDueOn(today)) {
                continue;
            }
            if (habit.getReminderTime() != null && habit.getReminderTime().isAfter(now)) {
                continue; // not yet time
            }
            if (checkinRepository.existsByHabitIdAndCheckinDate(habit.getId(), today)) {
                continue; // already done today
            }

            events.publishEvent(new HabitReminderDueEvent(habit.getUserId(), habit.getId(), habit.getName()));
            habit.setLastRemindedOn(today);
            habitRepository.save(habit);
        }
    }
}
