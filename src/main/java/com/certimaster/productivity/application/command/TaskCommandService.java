package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.TaskMapper;
import com.certimaster.productivity.adapter.web.response.TaskResponse;
import com.certimaster.productivity.domain.entity.TodoTask;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskCommandService {

    private final TodoTaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskCommandService(TodoTaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponse create(CreateTaskCommand command) {
        TodoTask task = TodoTask.builder()
                .userId(command.userId())
                .title(command.title())
                .description(command.description())
                .dueDate(command.dueDate())
                .build();
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse update(UpdateTaskCommand command) {
        TodoTask task = taskRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Task", command.id()));

        if (command.title() != null) {
            task.setTitle(command.title());
        }
        if (command.description() != null) {
            task.setDescription(command.description());
        }
        if (command.clearDueDate()) {
            task.setDueDate(null);
        } else if (command.dueDate() != null) {
            task.setDueDate(command.dueDate());
        }
        if (command.orderIndex() != null) {
            task.setOrderIndex(command.orderIndex());
        }
        // Status last so doneAt stamping reflects the final state.
        task.applyStatus(command.status());

        return taskMapper.toResponse(task);
    }

    public void reorder(ReorderTasksCommand command) {
        List<Long> ids = command.items().stream().map(ReorderTasksCommand.Item::id).toList();
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, TodoTask> owned = taskRepository.findByUserIdAndIdIn(command.userId(), ids).stream()
                .collect(Collectors.toMap(TodoTask::getId, Function.identity()));

        for (ReorderTasksCommand.Item item : command.items()) {
            TodoTask task = owned.get(item.id());
            if (task == null) {
                continue; // silently skip ids the user doesn't own
            }
            task.setOrderIndex(item.orderIndex());
            task.applyStatus(item.status());
        }
    }

    public void delete(DeleteTaskCommand command) {
        TodoTask task = taskRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Task", command.id()));
        taskRepository.delete(task);
    }
}
