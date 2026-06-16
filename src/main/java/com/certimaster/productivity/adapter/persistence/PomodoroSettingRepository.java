package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.PomodoroSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PomodoroSettingRepository extends JpaRepository<PomodoroSetting, Long> {

    Optional<PomodoroSetting> findByUserId(Long userId);
}
