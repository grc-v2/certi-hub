package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.PomodoroSessionRepository;
import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.PomodoroSessionMapper;
import com.certimaster.productivity.adapter.web.response.PomodoroSessionResponse;
import com.certimaster.productivity.domain.entity.PomodoroSession;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PomodoroSessionCommandService {

    private final PomodoroSessionRepository sessionRepository;
    private final TodoTaskRepository taskRepository;
    private final PomodoroSessionMapper sessionMapper;

    public PomodoroSessionCommandService(PomodoroSessionRepository sessionRepository,
                                         TodoTaskRepository taskRepository,
                                         PomodoroSessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.taskRepository = taskRepository;
        this.sessionMapper = sessionMapper;
    }

    public PomodoroSessionResponse record(RecordPomodoroSessionCommand command) {
        if (command.kind() == null) {
            throw BusinessException.invalidInput("kind is required (FOCUS, SHORT_BREAK or LONG_BREAK)");
        }
        if (command.startedAt() == null) {
            throw BusinessException.invalidInput("startedAt is required");
        }
        if (command.durationSeconds() <= 0) {
            throw BusinessException.invalidInput("durationSeconds must be positive");
        }
        if (command.taskId() != null) {
            // Owner check: linking a task the user doesn't own reads the same as a missing task.
            taskRepository.findByIdAndUserId(command.taskId(), command.userId())
                    .orElseThrow(() -> ResourceNotFoundException.byId("Task", command.taskId()));
        }

        PomodoroSession session = PomodoroSession.builder()
                .userId(command.userId())
                .taskId(command.taskId())
                .kind(command.kind())
                .startedAt(command.startedAt())
                .endedAt(LocalDateTime.now())
                .durationSeconds(command.durationSeconds())
                .completed(command.completed())
                .build();
        return sessionMapper.toResponse(sessionRepository.save(session));
    }
}
