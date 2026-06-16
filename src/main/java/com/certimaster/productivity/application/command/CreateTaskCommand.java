package com.certimaster.productivity.application.command;

import java.time.LocalDate;

public record CreateTaskCommand(
        Long userId,
        String title,
        String description,
        LocalDate dueDate
) {
}
