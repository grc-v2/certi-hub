package com.certimaster.learning.application.command;

public record UpdateQuestionStatusCommand(Long id, String status) {
}
