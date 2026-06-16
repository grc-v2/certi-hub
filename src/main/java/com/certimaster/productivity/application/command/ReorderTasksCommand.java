package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.TaskStatus;

import java.util.List;

/**
 * Persist a drag-and-drop rearrangement: each item carries a task's new column and
 * position. Applied atomically for the tasks the user owns.
 */
public record ReorderTasksCommand(Long userId, List<Item> items) {

    public record Item(Long id, TaskStatus status, int orderIndex) {
    }
}
