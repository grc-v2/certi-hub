package com.certimaster.productivity.application.command;

import com.certimaster.productivity.domain.entity.PomodoroPreset;

public record UpsertPomodoroSettingCommand(Long userId, PomodoroPreset preset) {
}
