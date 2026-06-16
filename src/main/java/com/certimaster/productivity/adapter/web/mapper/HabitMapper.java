package com.certimaster.productivity.adapter.web.mapper;

import com.certimaster.productivity.adapter.web.response.HabitResponse;
import com.certimaster.productivity.domain.entity.Habit;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HabitMapper {

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    public HabitResponse toResponse(Habit h, boolean dueToday, boolean checkedInToday, int currentStreak) {
        if (h == null) {
            return null;
        }
        List<String> weekdays = h.weekdaySet().stream()
                .sorted()
                .map(DayOfWeek::name)
                .toList();
        return HabitResponse.builder()
                .id(h.getId())
                .createdAt(h.getCreatedAt())
                .updatedAt(h.getUpdatedAt())
                .createdBy(h.getCreatedBy())
                .updatedBy(h.getUpdatedBy())
                .userId(h.getUserId())
                .name(h.getName())
                .color(h.getColor())
                .frequency(h.getFrequency() != null ? h.getFrequency().name() : null)
                .weekdays(weekdays)
                .reminderTime(h.getReminderTime() != null ? h.getReminderTime().format(HHMM) : null)
                .active(h.isActive())
                .orderIndex(h.getOrderIndex())
                .dueToday(dueToday)
                .checkedInToday(checkedInToday)
                .currentStreak(currentStreak)
                .build();
    }
}
