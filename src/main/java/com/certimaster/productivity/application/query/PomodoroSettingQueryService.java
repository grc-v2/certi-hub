package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.PomodoroSettingRepository;
import com.certimaster.productivity.adapter.web.mapper.PomodoroSettingMapper;
import com.certimaster.productivity.adapter.web.response.PomodoroSettingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PomodoroSettingQueryService {

    private final PomodoroSettingRepository settingRepository;
    private final PomodoroSettingMapper settingMapper;

    public PomodoroSettingQueryService(PomodoroSettingRepository settingRepository,
                                       PomodoroSettingMapper settingMapper) {
        this.settingRepository = settingRepository;
        this.settingMapper = settingMapper;
    }

    public PomodoroSettingResponse get(GetPomodoroSettingQuery query) {
        return settingRepository.findByUserId(query.userId())
                .map(settingMapper::toResponse)
                .orElseGet(settingMapper::defaultResponse);
    }
}
