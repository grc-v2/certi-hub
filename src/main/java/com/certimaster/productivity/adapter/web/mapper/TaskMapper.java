package com.certimaster.productivity.adapter.web.mapper;

import com.certimaster.productivity.adapter.web.response.TaskResponse;
import com.certimaster.productivity.domain.entity.TodoTask;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(TodoTask t) {
        if (t == null) {
            return null;
        }
        return TaskResponse.builder()
                .id(t.getId())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .createdBy(t.getCreatedBy())
                .updatedBy(t.getUpdatedBy())
                .userId(t.getUserId())
                .title(t.getTitle())
                .description(t.getDescription())
                .dueDate(t.getDueDate())
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .orderIndex(t.getOrderIndex())
                .doneAt(t.getDoneAt())
                .build();
    }
}
