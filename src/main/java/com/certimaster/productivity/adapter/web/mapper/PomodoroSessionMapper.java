package com.certimaster.productivity.adapter.web.mapper;

import com.certimaster.productivity.adapter.web.response.PomodoroSessionResponse;
import com.certimaster.productivity.domain.entity.PomodoroSession;
import org.springframework.stereotype.Component;

@Component
public class PomodoroSessionMapper {

    public PomodoroSessionResponse toResponse(PomodoroSession s) {
        if (s == null) {
            return null;
        }
        return PomodoroSessionResponse.builder()
                .id(s.getId())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .createdBy(s.getCreatedBy())
                .updatedBy(s.getUpdatedBy())
                .userId(s.getUserId())
                .taskId(s.getTaskId())
                .kind(s.getKind() != null ? s.getKind().name() : null)
                .startedAt(s.getStartedAt())
                .endedAt(s.getEndedAt())
                .durationSeconds(s.getDurationSeconds())
                .completed(s.isCompleted())
                .build();
    }
}
