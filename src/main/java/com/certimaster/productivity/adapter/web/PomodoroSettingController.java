package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.adapter.web.request.PomodoroSettingRequest;
import com.certimaster.productivity.adapter.web.response.PomodoroSettingResponse;
import com.certimaster.productivity.application.command.PomodoroSettingCommandService;
import com.certimaster.productivity.application.command.UpsertPomodoroSettingCommand;
import com.certimaster.productivity.application.query.GetPomodoroSettingQuery;
import com.certimaster.productivity.application.query.PomodoroSettingQueryService;
import com.certimaster.productivity.domain.entity.PomodoroPreset;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pomodoro/settings")
public class PomodoroSettingController {

    private final PomodoroSettingCommandService settingCommandService;
    private final PomodoroSettingQueryService settingQueryService;

    public PomodoroSettingController(PomodoroSettingCommandService settingCommandService,
                                     PomodoroSettingQueryService settingQueryService) {
        this.settingCommandService = settingCommandService;
        this.settingQueryService = settingQueryService;
    }

    @GetMapping
    public ResponseEntity<PomodoroSettingResponse> get() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(settingQueryService.get(new GetPomodoroSettingQuery(userId)));
    }

    @PutMapping
    public ResponseEntity<PomodoroSettingResponse> upsert(@RequestBody PomodoroSettingRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(settingCommandService.upsert(
                new UpsertPomodoroSettingCommand(userId, parsePreset(request.getPreset()))));
    }

    private static PomodoroPreset parsePreset(String raw) {
        if (raw == null || raw.isBlank()) {
            return PomodoroPreset.CLASSIC;
        }
        try {
            return PomodoroPreset.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PomodoroPreset.CLASSIC;
        }
    }
}
