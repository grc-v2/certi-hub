package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.PomodoroSettingRepository;
import com.certimaster.productivity.adapter.web.mapper.PomodoroSettingMapper;
import com.certimaster.productivity.adapter.web.response.PomodoroSettingResponse;
import com.certimaster.productivity.domain.entity.PomodoroSetting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PomodoroSettingCommandService {

    private final PomodoroSettingRepository settingRepository;
    private final PomodoroSettingMapper settingMapper;

    public PomodoroSettingCommandService(PomodoroSettingRepository settingRepository,
                                         PomodoroSettingMapper settingMapper) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
    }

    public PomodoroSettingResponse upsert(UpsertPomodoroSettingCommand command) {
        PomodoroSetting setting = settingRepository.findByUserId(command.userId())
                .orElseGet(() -> PomodoroSetting.forUser(command.userId(), command.preset()));
        setting.applyPreset(command.preset());
        return settingMapper.toResponse(settingRepository.save(setting));
    }
}
