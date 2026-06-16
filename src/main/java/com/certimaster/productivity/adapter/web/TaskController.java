package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.adapter.web.request.CreateTaskRequest;
import com.certimaster.productivity.adapter.web.request.ReorderTasksRequest;
import com.certimaster.productivity.adapter.web.request.UpdateTaskRequest;
import com.certimaster.productivity.adapter.web.response.TaskResponse;
import com.certimaster.productivity.application.command.CreateTaskCommand;
import com.certimaster.productivity.application.command.DeleteTaskCommand;
import com.certimaster.productivity.application.command.ReorderTasksCommand;
import com.certimaster.productivity.application.command.TaskCommandService;
import com.certimaster.productivity.application.command.UpdateTaskCommand;
import com.certimaster.productivity.application.query.ListTasksQuery;
import com.certimaster.productivity.application.query.TaskQueryService;
import com.certimaster.productivity.domain.entity.TaskStatus;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/todo-tasks")
public class TaskController {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;

    public TaskController(TaskCommandService taskCommandService, TaskQueryService taskQueryService) {
        this.taskCommandService = taskCommandService;
        this.taskQueryService = taskQueryService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(taskQueryService.list(new ListTasksQuery(userId)));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody CreateTaskRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        TaskResponse created = taskCommandService.create(new CreateTaskCommand(
                userId,
                request.getTitle(),
                request.getDescription(),
                parseDate(request.getDueDate())));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @RequestBody UpdateTaskRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean clearDueDate = request.getDueDate() != null && request.getDueDate().isBlank();
        TaskResponse updated = taskCommandService.update(new UpdateTaskCommand(
                id,
                userId,
                request.getTitle(),
                request.getDescription(),
                parseDate(request.getDueDate()),
                clearDueDate,
                parseStatus(request.getStatus()),
                request.getOrderIndex()));
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody ReorderTasksRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ReorderTasksCommand.Item> items = request.getItems() == null ? List.of() : request.getItems().stream()
                .map(i -> new ReorderTasksCommand.Item(
                        i.getId(),
                        parseStatus(i.getStatus()),
                        i.getOrderIndex() != null ? i.getOrderIndex() : 0))
                .toList();
        taskCommandService.reorder(new ReorderTasksCommand(userId, items));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        taskCommandService.delete(new DeleteTaskCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    private static LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalDate.parse(raw.trim());
    }

    private static TaskStatus parseStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return TaskStatus.valueOf(raw.trim().toUpperCase());
    }
}
