package com.certimaster.productivity.domain.event;

import java.io.Serial;
import java.io.Serializable;

/**
 * Published by the reminder scheduler when a habit is due, has reached its reminder
 * time, and has not yet been checked in today. The engagement module turns this into
 * an in-app notification; productivity itself has no dependency on notifications.
 */
public class HabitReminderDueEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long habitId;
    private String habitName;

    public HabitReminderDueEvent() {
    }

    public HabitReminderDueEvent(Long userId, Long habitId, String habitName) {
        this.userId = userId;
        this.habitId = habitId;
        this.habitName = habitName;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }
}
