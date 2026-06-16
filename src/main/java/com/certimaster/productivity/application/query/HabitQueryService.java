package com.certimaster.productivity.application.query;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HabitQueryService {

    /** How far back streaks are computed; bounds the per-user check-in scan. */
    private static final int STREAK_WINDOW_DAYS = 366;

    private final HabitRepository habitRepository;
    private final HabitCheckinRepository checkinRepository;
    private final HabitMapper habitMapper;
    private final HabitStreakCalculator streakCalculator;

    public HabitQueryService(HabitRepository habitRepository,
                             HabitCheckinRepository checkinRepository,
                             HabitMapper habitMapper,
                             HabitStreakCalculator streakCalculator) {
        this.habitRepository = habitRepository;
        this.checkinRepository = checkinRepository;
        this.habitMapper = habitMapper;
        this.streakCalculator = streakCalculator;
    }

    public List<HabitResponse> list(ListHabitsQuery query) {
        LocalDate today = LocalDate.now();
        List<Habit> habits = habitRepository.findByUserIdOrderByOrderIndexAscCreatedAtAsc(query.userId());

        // One scan of recent check-ins, grouped by habit, then computed in memory.
        Map<Long, Set<LocalDate>> byHabit = checkinRepository
                .findByUserIdAndCheckinDateGreaterThanEqual(query.userId(), today.minusDays(STREAK_WINDOW_DAYS))
                .stream()
                .collect(Collectors.groupingBy(
                        HabitCheckin::getHabitId,
                        Collectors.mapping(HabitCheckin::getCheckinDate, Collectors.toSet())));

        return habits.stream()
                .map(h -> {
                    Set<LocalDate> checkins = byHabit.getOrDefault(h.getId(), Set.of());
                    boolean dueToday = h.isDueOn(today);
                    boolean checkedInToday = checkins.contains(today);
                    int streak = streakCalculator.currentStreak(h, checkins, today);
                    return habitMapper.toResponse(h, dueToday, checkedInToday, streak);
                })
                .toList();
    }

    /** Check-in dates (ISO strings) in [from, to] for one habit, oldest first. */
    public List<String> history(GetHabitHistoryQuery query) {
        Habit habit = habitRepository.findByIdAndUserId(query.habitId(), query.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Habit", query.habitId()));
        return checkinRepository
                .findByHabitIdAndCheckinDateBetween(habit.getId(), query.from(), query.to())
                .stream()
                .map(HabitCheckin::getCheckinDate)
                .sorted()
                .map(LocalDate::toString)
                .toList();
    }
}
