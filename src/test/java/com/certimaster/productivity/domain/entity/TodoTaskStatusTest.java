package com.certimaster.productivity.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TodoTaskStatusTest {

    @Test
    void enteringDoneStampsDoneAt() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").status(TaskStatus.TODO).build();
        assertThat(task.getDoneAt()).isNull();

        task.applyStatus(TaskStatus.DONE);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getDoneAt()).isNotNull();
    }

    @Test
    void leavingDoneClearsDoneAt() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").status(TaskStatus.DONE).build();
        assertThat(task.getDoneAt()).isNotNull();

        task.applyStatus(TaskStatus.DOING);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.DOING);
        assertThat(task.getDoneAt()).isNull();
    }

    @Test
    void reEnteringDoneKeepsOriginalStamp() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").status(TaskStatus.DONE).build();
        var original = task.getDoneAt();

        task.applyStatus(TaskStatus.DONE); // no-op, same status

        assertThat(task.getDoneAt()).isEqualTo(original);
    }

    @Test
    void nullStatusIsIgnored() {
        TodoTask task = TodoTask.builder().userId(1L).title("x").status(TaskStatus.TODO).build();
        task.applyStatus(null);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TODO);
    }
}
