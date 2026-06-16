package com.certimaster.productivity.adapter.web.request;

import java.util.List;

/**
 * Create/update payload for a habit. Used for both POST (create) and PATCH (partial
 * update); on PATCH, null fields mean "leave unchanged". {@code reminderTime} is an
 * "HH:mm" string, empty string clears it. {@code weekdays} are {@link java.time.DayOfWeek}
 * names (e.g. "MONDAY").
 */
public class HabitRequest {

    private String name;
    private String color;
    private String frequency;
    private List<String> weekdays;
    private String reminderTime;
    private Boolean active;
    private Integer orderIndex;

    public HabitRequest() {
    }

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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
