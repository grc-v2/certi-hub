package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.adapter.web.request.HabitRequest;
import com.certimaster.productivity.adapter.web.response.HabitResponse;
import com.certimaster.productivity.application.command.CreateHabitCommand;
import com.certimaster.productivity.application.command.DeleteHabitCommand;
import com.certimaster.productivity.application.command.HabitCommandService;
import com.certimaster.productivity.application.command.SetHabitCheckinCommand;
import com.certimaster.productivity.application.command.UpdateHabitCommand;
import com.certimaster.productivity.application.query.GetHabitHistoryQuery;
import com.certimaster.productivity.application.query.HabitQueryService;
import com.certimaster.productivity.application.query.ListHabitsQuery;
import com.certimaster.productivity.domain.entity.HabitFrequency;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {

    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    private final HabitCommandService habitCommandService;
    private final HabitQueryService habitQueryService;

    public HabitController(HabitCommandService habitCommandService, HabitQueryService habitQueryService) {
        this.habitCommandService = habitCommandService;
        this.habitQueryService = habitQueryService;
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(habitQueryService.list(new ListHabitsQuery(userId)));
    }

    @PostMapping
    public ResponseEntity<HabitResponse> create(@RequestBody HabitRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        HabitFrequency frequency = parseFrequency(request.getFrequency());
        HabitResponse created = habitCommandService.create(new CreateHabitCommand(
                userId,
                request.getName(),
                request.getColor(),
                frequency != null ? frequency : HabitFrequency.DAILY,
                parseWeekdays(request.getWeekdays()),
                parseTime(request.getReminderTime())));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HabitResponse> update(@PathVariable Long id, @RequestBody HabitRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean clearReminder = request.getReminderTime() != null && request.getReminderTime().isBlank();
        HabitResponse updated = habitCommandService.update(new UpdateHabitCommand(
                id,
                userId,
                request.getName(),
                request.getColor(),
                parseFrequency(request.getFrequency()),
                request.getWeekdays() != null ? parseWeekdays(request.getWeekdays()) : null,
                parseTime(request.getReminderTime()),
                clearReminder,
                request.getActive(),
                request.getOrderIndex()));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        habitCommandService.delete(new DeleteHabitCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkin")
    public ResponseEntity<HabitResponse> checkin(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(habitCommandService.setCheckin(
                new SetHabitCheckinCommand(id, userId, date != null ? date : LocalDate.now(), true)));
    }

    @DeleteMapping("/{id}/checkin")
    public ResponseEntity<HabitResponse> uncheckin(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(habitCommandService.setCheckin(
                new SetHabitCheckinCommand(id, userId, date != null ? date : LocalDate.now(), false)));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<String>> history(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(habitQueryService.history(new GetHabitHistoryQuery(id, userId, from, to)));
    }

    private static HabitFrequency parseFrequency(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return HabitFrequency.valueOf(raw.trim().toUpperCase());
    }

    private static Set<DayOfWeek> parseWeekdays(List<String> raw) {
        Set<DayOfWeek> days = new LinkedHashSet<>();
        if (raw == null) {
            return days;
        }
        for (String s : raw) {
            if (s != null && !s.isBlank()) {
                days.add(DayOfWeek.valueOf(s.trim().toUpperCase()));
            }
        }
        return days;
    }

    private static LocalTime parseTime(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalTime.parse(raw.trim(), HHMM);
    }
}
