package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.PomodoroSessionRepository;
import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.PomodoroSessionMapper;
import com.certimaster.productivity.adapter.web.response.PomodoroStatsResponse;
import com.certimaster.productivity.domain.entity.PomodoroSession;
import com.certimaster.productivity.domain.entity.PomodoroSessionKind;
import com.certimaster.productivity.domain.entity.TodoTask;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PomodoroSessionQueryServiceTest {

    @Mock
    private PomodoroSessionRepository sessionRepository;

    @Mock
    private TodoTaskRepository taskRepository;

    @Mock
    private PomodoroSessionMapper sessionMapper;

    @InjectMocks
    private PomodoroSessionQueryService service;

    @Test
    void statsAggregatesCompletedFocusSessionsIntoSevenDayBreakdown() {
        LocalDate today = LocalDate.now();
        when(sessionRepository.findByUserIdAndStartedAtGreaterThanEqual(eq(7L), any())).thenReturn(List.of(
                focus(today.atTime(9, 0), 1500, true),
                focus(today.atTime(14, 0), 1500, true),
                focus(today.minusDays(2).atTime(10, 0), 3000, true),
                focus(today.minusDays(2).atTime(11, 0), 1500, false),           // abandoned: excluded
                session(PomodoroSessionKind.SHORT_BREAK, today.atTime(9, 30), 300, true))); // break: excluded

        PomodoroStatsResponse stats = service.stats(new GetPomodoroStatsQuery(7L, null));

        assertThat(stats.getTodayFocusSessions()).isEqualTo(2);
        assertThat(stats.getTodayFocusSeconds()).isEqualTo(3000);
        assertThat(stats.getWeekFocusSessions()).isEqualTo(3);
        assertThat(stats.getWeekFocusSeconds()).isEqualTo(6000);
        assertThat(stats.getTaskId()).isNull();
        assertThat(stats.getTaskFocusSessions()).isNull();

        assertThat(stats.getDays()).hasSize(7);
        assertThat(stats.getDays().get(0).getDate()).isEqualTo(today.minusDays(6).toString());
        assertThat(stats.getDays().get(6).getDate()).isEqualTo(today.toString());
        assertThat(stats.getDays().get(6).getFocusSessions()).isEqualTo(2);
        assertThat(stats.getDays().get(4).getFocusSessions()).isEqualTo(1);
        assertThat(stats.getDays().get(4).getFocusSeconds()).isEqualTo(3000);
        assertThat(stats.getDays().get(0).getFocusSessions()).isZero();
    }

    @Test
    void statsZeroFillsWhenNoSessionsRecorded() {
        when(sessionRepository.findByUserIdAndStartedAtGreaterThanEqual(eq(7L), any())).thenReturn(List.of());

        PomodoroStatsResponse stats = service.stats(new GetPomodoroStatsQuery(7L, null));

        assertThat(stats.getTodayFocusSessions()).isZero();
        assertThat(stats.getWeekFocusSeconds()).isZero();
        assertThat(stats.getDays()).hasSize(7);
        assertThat(stats.getDays()).allSatisfy(day -> {
            assertThat(day.getFocusSessions()).isZero();
            assertThat(day.getFocusSeconds()).isZero();
        });
    }

    @Test
    void statsIncludesPerTaskCountWhenTaskFilterGiven() {
        when(sessionRepository.findByUserIdAndStartedAtGreaterThanEqual(eq(7L), any())).thenReturn(List.of());
        when(taskRepository.findByIdAndUserId(42L, 7L)).thenReturn(Optional.of(new TodoTask()));
        when(sessionRepository.countByUserIdAndTaskIdAndKindAndCompletedTrue(7L, 42L, PomodoroSessionKind.FOCUS))
                .thenReturn(5L);

        PomodoroStatsResponse stats = service.stats(new GetPomodoroStatsQuery(7L, 42L));

        assertThat(stats.getTaskId()).isEqualTo(42L);
        assertThat(stats.getTaskFocusSessions()).isEqualTo(5L);
    }

    @Test
    void statsRejectsTaskFilterNotOwnedByUser() {
        when(sessionRepository.findByUserIdAndStartedAtGreaterThanEqual(eq(7L), any())).thenReturn(List.of());
        when(taskRepository.findByIdAndUserId(42L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.stats(new GetPomodoroStatsQuery(7L, 42L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private static PomodoroSession focus(LocalDateTime startedAt, int durationSeconds, boolean completed) {
        return session(PomodoroSessionKind.FOCUS, startedAt, durationSeconds, completed);
    }

    private static PomodoroSession session(PomodoroSessionKind kind, LocalDateTime startedAt,
                                           int durationSeconds, boolean completed) {
        return PomodoroSession.builder()
                .userId(7L)
                .kind(kind)
                .startedAt(startedAt)
                .endedAt(startedAt.plusSeconds(durationSeconds))
                .durationSeconds(durationSeconds)
                .completed(completed)
                .build();
    }
}
