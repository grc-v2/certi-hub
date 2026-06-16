package com.certimaster.productivity.adapter.web.mapper;

import com.certimaster.productivity.adapter.web.response.PomodoroSettingResponse;
import com.certimaster.productivity.domain.entity.PomodoroPreset;
import com.certimaster.productivity.domain.entity.PomodoroSetting;
import org.springframework.stereotype.Component;

@Component
public class PomodoroSettingMapper {

    public PomodoroSettingResponse toResponse(PomodoroSetting s) {
        return new PomodoroSettingResponse(s.getPreset().name(), s.getFocusMinutes(), s.getBreakMinutes());
    }

    /** Default (unpersisted) settings used when a user has not chosen a preset yet. */
    public PomodoroSettingResponse defaultResponse() {
        PomodoroPreset preset = PomodoroPreset.CLASSIC;
        return new PomodoroSettingResponse(preset.name(), preset.getFocusMinutes(), preset.getBreakMinutes());
    }
}
