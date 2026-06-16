package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.TaskStatus;

import java.time.LocalDate;

/**
 * Partial update of a task. Each non-null field is applied; {@code status} is moved
 * through the aggregate so {@code doneAt} stays correct. {@code clearDueDate} lets the
 * caller distinguish "leave due date" (false) from "remove it" (true).
 */
public record UpdateTaskCommand(
        Long id,
        Long userId,
        String title,
        String description,
        LocalDate dueDate,
        boolean clearDueDate,
        TaskStatus status,
        Integer orderIndex
) {
}
