package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * A user's saved Pomodoro preference (one row per user). The focus/break minutes
 * are derived from the chosen {@link PomodoroPreset} so they always stay in sync.
 */
@Entity
@Table(name = "pomodoro_settings")
public class PomodoroSetting extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "preset", nullable = false, length = 16)
    private PomodoroPreset preset = PomodoroPreset.CLASSIC;

    @Column(name = "focus_minutes", nullable = false)
    private int focusMinutes = PomodoroPreset.CLASSIC.getFocusMinutes();

    @Column(name = "break_minutes", nullable = false)
    private int breakMinutes = PomodoroPreset.CLASSIC.getBreakMinutes();

    public PomodoroSetting() {
        super();
    }

    public static PomodoroSetting forUser(Long userId, PomodoroPreset preset) {
        PomodoroSetting s = new PomodoroSetting();
        s.userId = userId;
        s.applyPreset(preset);
        return s;
    }

    /** Switch preset and recompute the focus/break minutes from it. */
    public void applyPreset(PomodoroPreset newPreset) {
        this.preset = newPreset;
        this.focusMinutes = newPreset.getFocusMinutes();
        this.breakMinutes = newPreset.getBreakMinutes();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public PomodoroPreset getPreset() { return preset; }

    public int getFocusMinutes() { return focusMinutes; }

    public int getBreakMinutes() { return breakMinutes; }
}
