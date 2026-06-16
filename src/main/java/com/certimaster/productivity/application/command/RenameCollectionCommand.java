package com.certimaster.productivity.application.command;

public record RenameCollectionCommand(Long id, Long userId, String name) {
}
