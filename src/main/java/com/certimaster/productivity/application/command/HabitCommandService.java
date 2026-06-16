package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.HabitCheckinRepository;
import com.certimaster.productivity.adapter.persistence.HabitRepository;
import com.certimaster.productivity.adapter.web.mapper.HabitMapper;
import com.certimaster.productivity.adapter.web.response.HabitResponse;
import com.certimaster.productivity.domain.entity.Habit;
import com.certimaster.productivity.domain.entity.HabitCheckin;
import com.certimaster.productivity.domain.service.HabitStreakCalculator;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class HabitCommandService {

    private final HabitRepository habitRepository;
    private final HabitCheckinRepository checkinRepository;
    private final HabitMapper habitMapper;
    private final HabitStreakCalculator streakCalculator;

    public HabitCommandService(HabitRepository habitRepository,
                               HabitCheckinRepository checkinRepository,
                               HabitMapper habitMapper,
                               HabitStreakCalculator streakCalculator) {
        this.habitRepository = habitRepository;
        this.checkinRepository = checkinRepository;
        this.habitMapper = habitMapper;
        this.streakCalculator = streakCalculator;
    }

    public HabitResponse create(CreateHabitCommand command) {
        Habit habit = Habit.builder()
                .userId(command.userId())
                .name(command.name())
                .color(command.color())
                .frequency(command.frequency())
                .weekdays(command.weekdays())
                .reminderTime(command.reminderTime())
                .build();
        return toResponseWithToday(habitRepository.save(habit), LocalDate.now());
    }

    public HabitResponse update(UpdateHabitCommand command) {
        Habit habit = habitRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Habit", command.id()));

        if (command.name() != null) {
            habit.setName(command.name());
        }
        if (command.color() != null) {
            habit.setColor(command.color());
        }
        if (command.frequency() != null) {
            habit.setFrequency(command.frequency());
        }
        if (command.weekdays() != null) {
            habit.setWeekdaySet(command.weekdays());
        }
        if (command.clearReminder()) {
            habit.setReminderTime(null);
        } else if (command.reminderTime() != null) {
            habit.setReminderTime(command.reminderTime());
        }
        if (command.active() != null) {
            habit.setActive(command.active());
        }
        if (command.orderIndex() != null) {
            habit.setOrderIndex(command.orderIndex());
        }
        return toResponseWithToday(habit, LocalDate.now());
    }

    public void delete(DeleteHabitCommand command) {
        Habit habit = habitRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Habit", command.id()));
        checkinRepository.deleteByHabitId(habit.getId());
        habitRepository.delete(habit);
    }

    public HabitResponse setCheckin(SetHabitCheckinCommand command) {
        Habit habit = habitRepository.findByIdAndUserId(command.habitId(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Habit", command.habitId()));
        LocalDate date = command.date() != null ? command.date() : LocalDate.now();

        if (command.done()) {
            if (!checkinRepository.existsByHabitIdAndCheckinDate(habit.getId(), date)) {
                checkinRepository.save(new HabitCheckin(habit.getId(), habit.getUserId(), date));
            }
        } else {
            checkinRepository.findByHabitIdAndCheckinDate(habit.getId(), date)
                    .ifPresent(checkinRepository::delete);
        }
        return toResponseWithToday(habit, LocalDate.now());
    }

    private HabitResponse toResponseWithToday(Habit habit, LocalDate today) {
        Set<LocalDate> checkins = checkinRepository
                .findByHabitIdAndCheckinDateBetween(habit.getId(), today.minusDays(366), today)
                .stream()
                .map(HabitCheckin::getCheckinDate)
                .collect(Collectors.toSet());
        boolean dueToday = habit.isDueOn(today);
        boolean checkedInToday = checkins.contains(today);
        int streak = streakCalculator.currentStreak(habit, checkins, today);
        return habitMapper.toResponse(habit, dueToday, checkedInToday, streak);
    }
}
