package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.TaskMapper;
import com.certimaster.productivity.domain.entity.TaskStatus;
import com.certimaster.productivity.domain.entity.TodoTask;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskCommandServiceTest {

    @Mock
    private TodoTaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskCommandService service;

    @Test
    void createBuildsBacklogTaskForUser() {
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateTaskCommand(7L, "Ôn thi SAA", "chapter 3", LocalDate.of(2026, 7, 20)));

        ArgumentCaptor<TodoTask> captor = ArgumentCaptor.forClass(TodoTask.class);
        verify(taskRepository).save(captor.capture());
        TodoTask saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(7L);
        assertThat(saved.getTitle()).isEqualTo("Ôn thi SAA");
        assertThat(saved.getDescription()).isEqualTo("chapter 3");
        assertThat(saved.getDueDate()).isEqualTo(LocalDate.of(2026, 7, 20));
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.BACKLOG);
        assertThat(saved.getDoneAt()).isNull();
    }

    @Test
    void updateAppliesOnlyNonNullFields() {
        TodoTask task = TodoTask.builder()
                .userId(1L).title("old").description("keep me")
                .dueDate(LocalDate.of(2026, 7, 15)).status(TaskStatus.TODO).orderIndex(3)
                .build();
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(task));

        service.update(new UpdateTaskCommand(5L, 1L, "new title", null, null, false, null, null));

        assertThat(task.getTitle()).isEqualTo("new title");
        assertThat(task.getDescription()).isEqualTo("keep me");
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2026, 7, 15));
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(task.getOrderIndex()).isEqualTo(3);
    }

    @Test
    void updateMovingToDoneStampsDoneAt() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").status(TaskStatus.DOING).build();
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(task));

        service.update(new UpdateTaskCommand(5L, 1L, null, null, null, false, TaskStatus.DONE, null));

        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getDoneAt()).isNotNull();
    }

    @Test
    void updateClearDueDateWinsOverProvidedDueDate() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").dueDate(LocalDate.of(2026, 7, 15)).build();
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(task));

        service.update(new UpdateTaskCommand(5L, 1L, null, null, LocalDate.of(2026, 8, 1), true, null, null));

        assertThat(task.getDueDate()).isNull();
    }

    @Test
    void updateUnknownOrForeignTaskFails() {
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(
                new UpdateTaskCommand(5L, 1L, "t", null, null, false, null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reorderAppliesNewColumnAndPositionToOwnedTasks() {
        TodoTask a = TodoTask.builder().userId(1L).title("a").status(TaskStatus.TODO).orderIndex(0).build();
        a.setId(10L);
        TodoTask b = TodoTask.builder().userId(1L).title("b").status(TaskStatus.DOING).orderIndex(1).build();
        b.setId(11L);
        when(taskRepository.findByUserIdAndIdIn(1L, List.of(10L, 11L))).thenReturn(List.of(a, b));

        service.reorder(new ReorderTasksCommand(1L, List.of(
                new ReorderTasksCommand.Item(10L, TaskStatus.DONE, 0),
                new ReorderTasksCommand.Item(11L, TaskStatus.TODO, 2))));

        assertThat(a.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(a.getDoneAt()).isNotNull();
        assertThat(b.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(b.getOrderIndex()).isEqualTo(2);
    }

    @Test
    void reorderSilentlySkipsTasksTheUserDoesNotOwn() {
        TodoTask mine = TodoTask.builder().userId(1L).title("mine").status(TaskStatus.TODO).build();
        mine.setId(10L);
        // Repository only returns the task the user owns; id 99 belongs to someone else.
        when(taskRepository.findByUserIdAndIdIn(1L, List.of(10L, 99L))).thenReturn(List.of(mine));

        service.reorder(new ReorderTasksCommand(1L, List.of(
                new ReorderTasksCommand.Item(10L, TaskStatus.DOING, 1),
                new ReorderTasksCommand.Item(99L, TaskStatus.DONE, 0))));

        assertThat(mine.getStatus()).isEqualTo(TaskStatus.DOING);
        assertThat(mine.getOrderIndex()).isEqualTo(1);
    }

    @Test
    void reorderWithNoItemsDoesNotTouchRepository() {
        service.reorder(new ReorderTasksCommand(1L, List.of()));

        verify(taskRepository, never()).findByUserIdAndIdIn(any(), any());
    }

    @Test
    void deleteRemovesOwnedTask() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").build();
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(task));

        service.delete(new DeleteTaskCommand(5L, 1L));

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteUnknownOrForeignTaskFails() {
        when(taskRepository.findByIdAndUserId(5L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(new DeleteTaskCommand(5L, 2L)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(taskRepository, never()).delete(any(TodoTask.class));
    }
}
