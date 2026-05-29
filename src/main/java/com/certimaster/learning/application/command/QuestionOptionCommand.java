package com.certimaster.learning.application.command;

public record QuestionOptionCommand(
        String content,
        Boolean isCorrect,
        Integer orderIndex
) {
}
