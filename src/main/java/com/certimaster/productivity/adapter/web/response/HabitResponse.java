package com.certimaster.productivity.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.util.List;

public class HabitResponse extends BaseResponse {

    private Long userId;
    private String name;
    private String color;
    private String frequency;
    private List<String> weekdays;
    private String reminderTime;
    private boolean active;
    private int orderIndex;

    // Derived for the requesting day
    private boolean dueToday;
    private boolean checkedInToday;
    private int currentStreak;

    public HabitResponse() {
        super();
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<String> getWeekdays() { return weekdays; }
    public void setWeekdays(List<String> weekdays) { this.weekdays = weekdays; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public boolean isDueToday() { return dueToday; }
    public void setDueToday(boolean dueToday) { this.dueToday = dueToday; }

    public boolean isCheckedInToday() { return checkedInToday; }
    public void setCheckedInToday(boolean checkedInToday) { this.checkedInToday = checkedInToday; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final HabitResponse o = new HabitResponse();

        public Builder id(Long v) { o.setId(v); return this; }
        public Builder createdAt(java.time.LocalDateTime v) { o.setCreatedAt(v); return this; }
        public Builder updatedAt(java.time.LocalDateTime v) { o.setUpdatedAt(v); return this; }
        public Builder createdBy(String v) { o.setCreatedBy(v); return this; }
        public Builder updatedBy(String v) { o.setUpdatedBy(v); return this; }
        public Builder userId(Long v) { o.userId = v; return this; }
        public Builder name(String v) { o.name = v; return this; }
        public Builder color(String v) { o.color = v; return this; }
        public Builder frequency(String v) { o.frequency = v; return this; }
        public Builder weekdays(List<String> v) { o.weekdays = v; return this; }
        public Builder reminderTime(String v) { o.reminderTime = v; return this; }
        public Builder active(boolean v) { o.active = v; return this; }
        public Builder orderIndex(int v) { o.orderIndex = v; return this; }
        public Builder dueToday(boolean v) { o.dueToday = v; return this; }
        public Builder checkedInToday(boolean v) { o.checkedInToday = v; return this; }
        public Builder currentStreak(int v) { o.currentStreak = v; return this; }

        public HabitResponse build() { return o; }
    }
}
