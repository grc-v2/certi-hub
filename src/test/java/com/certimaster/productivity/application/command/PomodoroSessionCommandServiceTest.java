package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.PomodoroSessionRepository;
import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.PomodoroSessionMapper;
import com.certimaster.productivity.adapter.web.response.PomodoroSessionResponse;
import com.certimaster.productivity.domain.entity.PomodoroSession;
import com.certimaster.productivity.domain.entity.PomodoroSessionKind;
import com.certimaster.productivity.domain.entity.TodoTask;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PomodoroSessionCommandServiceTest {

    @Mock
    private PomodoroSessionRepository sessionRepository;

    @Mock
    private TodoTaskRepository taskRepository;

    @Mock
    private PomodoroSessionMapper sessionMapper;

    @InjectMocks
    private PomodoroSessionCommandService service;

    private static final LocalDateTime STARTED = LocalDateTime.of(2026, 7, 11, 9, 0);

    @Test
    void recordSavesCompletedFocusSessionWithoutTask() {
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(sessionMapper.toResponse(any())).thenReturn(new PomodoroSessionResponse());

        service.record(new RecordPomodoroSessionCommand(7L, null, PomodoroSessionKind.FOCUS, STARTED, 1500, true));

        PomodoroSession saved = captureSaved();
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getTaskId()).isNull();
        assertThat(saved.getKind()).isEqualTo(PomodoroSessionKind.FOCUS);
        assertThat(saved.getStartedAt()).isEqualTo(STARTED);
        assertThat(saved.getEndedAt()).isNotNull();
        assertThat(saved.getDurationSeconds()).isEqualTo(1500);
        assertThat(saved.isCompleted()).isTrue();
    }

    @Test
    void recordLinksTaskWhenOwnedByUser() {
        when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(sessionMapper.toResponse(any())).thenReturn(new PomodoroSessionResponse());
        when(taskRepository.findByIdAndUserId(42L, 7L)).thenReturn(Optional.of(new TodoTask()));

        service.record(new RecordPomodoroSessionCommand(7L, 42L, PomodoroSessionKind.FOCUS, STARTED, 1500, false));

        PomodoroSession saved = captureSaved();
        assertThat(saved.getTaskId()).isEqualTo(42L);
        assertThat(saved.isCompleted()).isFalse();
    }

    @Test
    void recordRejectsTaskNotOwnedByUser() {
        when(taskRepository.findByIdAndUserId(42L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.record(
                new RecordPomodoroSessionCommand(7L, 42L, PomodoroSessionKind.FOCUS, STARTED, 1500, true)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void recordRejectsMissingKind() {
        assertThatThrownBy(() -> service.record(
                new RecordPomodoroSessionCommand(7L, null, null, STARTED, 1500, true)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("kind");
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void recordRejectsMissingStartedAt() {
        assertThatThrownBy(() -> service.record(
                new RecordPomodoroSessionCommand(7L, null, PomodoroSessionKind.FOCUS, null, 1500, true)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("startedAt");
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void recordRejectsNonPositiveDuration() {
        assertThatThrownBy(() -> service.record(
                new RecordPomodoroSessionCommand(7L, null, PomodoroSessionKind.FOCUS, STARTED, 0, true)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("durationSeconds");
        verify(sessionRepository, never()).save(any());
    }

    private PomodoroSession captureSaved() {
        ArgumentCaptor<PomodoroSession> captor = ArgumentCaptor.forClass(PomodoroSession.class);
        verify(sessionRepository).save(captor.capture());
        return captor.getValue();
    }
}
