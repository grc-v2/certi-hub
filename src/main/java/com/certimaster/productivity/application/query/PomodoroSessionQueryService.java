package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.PomodoroSessionRepository;
import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.PomodoroSessionMapper;
import com.certimaster.productivity.adapter.web.response.PomodoroSessionResponse;
import com.certimaster.productivity.adapter.web.response.PomodoroStatsResponse;
import com.certimaster.productivity.domain.entity.PomodoroSession;
import com.certimaster.productivity.domain.entity.PomodoroSessionKind;
import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PomodoroSessionQueryService {

    /** Stats window: today plus the six previous days. */
    private static final int STATS_WINDOW_DAYS = 7;

    private final PomodoroSessionRepository sessionRepository;
    private final TodoTaskRepository taskRepository;
    private final PomodoroSessionMapper sessionMapper;

    public PomodoroSessionQueryService(PomodoroSessionRepository sessionRepository,
                                       TodoTaskRepository taskRepository,
                                       PomodoroSessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.taskRepository = taskRepository;
        this.sessionMapper = sessionMapper;
    }

    public PageResponse<PomodoroSessionResponse> history(ListPomodoroSessionsQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<PomodoroSession> page = query.taskId() != null
                ? sessionRepository.findByUserIdAndTaskIdOrderByStartedAtDesc(query.userId(), query.taskId(), pageable)
                : sessionRepository.findByUserIdOrderByStartedAtDesc(query.userId(), pageable);
        return PageResponse.of(page.map(sessionMapper::toResponse));
    }

    /**
     * One scan of the last 7 days of sessions, aggregated in memory (mirrors the
     * HabitQueryService approach). Only completed FOCUS sessions count.
     */
    public PomodoroStatsResponse stats(GetPomodoroStatsQuery query) {
        LocalDate today = LocalDate.now();
        LocalDate windowStart = today.minusDays(STATS_WINDOW_DAYS - 1L);

        Map<LocalDate, List<PomodoroSession>> focusByDay = sessionRepository
                .findByUserIdAndStartedAtGreaterThanEqual(query.userId(), windowStart.atStartOfDay())
                .stream()
                .filter(PomodoroSession::isCompleted)
                .filter(s -> s.getKind() == PomodoroSessionKind.FOCUS)
                .collect(Collectors.groupingBy(s -> s.getStartedAt().toLocalDate()));

        List<PomodoroStatsResponse.DayStat> days = new ArrayList<>(STATS_WINDOW_DAYS);
        long weekSessions = 0;
        long weekSeconds = 0;
        for (LocalDate day = windowStart; !day.isAfter(today); day = day.plusDays(1)) {
            List<PomodoroSession> sessions = focusByDay.getOrDefault(day, List.of());
            long seconds = sessions.stream().mapToLong(PomodoroSession::getDurationSeconds).sum();
            days.add(new PomodoroStatsResponse.DayStat(day.toString(), sessions.size(), seconds));
            weekSessions += sessions.size();
            weekSeconds += seconds;
        }
        PomodoroStatsResponse.DayStat todayStat = days.get(days.size() - 1);

        PomodoroStatsResponse response = new PomodoroStatsResponse();
        response.setTodayFocusSessions(todayStat.getFocusSessions());
        response.setTodayFocusSeconds(todayStat.getFocusSeconds());
        response.setWeekFocusSessions(weekSessions);
        response.setWeekFocusSeconds(weekSeconds);
        response.setDays(days);

        if (query.taskId() != null) {
            // Owner check so one user can't probe another user's task ids for counts.
            taskRepository.findByIdAndUserId(query.taskId(), query.userId())
                    .orElseThrow(() -> ResourceNotFoundException.byId("Task", query.taskId()));
            response.setTaskId(query.taskId());
            response.setTaskFocusSessions(sessionRepository.countByUserIdAndTaskIdAndKindAndCompletedTrue(
                    query.userId(), query.taskId(), PomodoroSessionKind.FOCUS));
        }
        return response;
    }
}
