package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A good habit the user wants to keep. Owns the rule for whether it is "due" on a
 * given day (driven by {@link #frequency} and {@link #weekdays}) and the
 * {@code lastRemindedOn} stamp the reminder scheduler uses to fire at most once a day.
 */
@Entity
@Table(name = "habits")
public class Habit extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /** Optional accent colour (hex) chosen in the UI. */
    @Column(name = "color", length = 20)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 16)
    private HabitFrequency frequency = HabitFrequency.DAILY;

    /** CSV of {@link DayOfWeek} names (e.g. "MONDAY,WEDNESDAY"); only used when frequency = WEEKLY. */
    @Column(name = "weekdays", length = 120)
    private String weekdays;

    @Column(name = "reminder_time")
    private LocalTime reminderTime;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    /** Last date the reminder scheduler fired for this habit; guards against duplicates. */
    @Column(name = "last_reminded_on")
    private LocalDate lastRemindedOn;

    public Habit() {
        super();
    }

    /** The configured weekdays as a set (empty when frequency is not WEEKLY or none set). */
    public Set<DayOfWeek> weekdaySet() {
        if (weekdays == null || weekdays.isBlank()) {
            return EnumSet.noneOf(DayOfWeek.class);
        }
        return Arrays.stream(weekdays.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));
    }

    public void setWeekdaySet(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            this.weekdays = null;
            return;
        }
        this.weekdays = days.stream()
                .sorted()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(","));
    }

    /** Whether the habit is expected to be done on the given date. */
    public boolean isDueOn(LocalDate date) {
        if (!active) {
            return false;
        }
        return switch (frequency) {
            case DAILY -> true;
            case WEEKLY -> weekdaySet().contains(date.getDayOfWeek());
        };
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public HabitFrequency getFrequency() { return frequency; }
    public void setFrequency(HabitFrequency frequency) { this.frequency = frequency; }

    public String getWeekdays() { return weekdays; }
    public void setWeekdays(String weekdays) { this.weekdays = weekdays; }

    public LocalTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalTime reminderTime) { this.reminderTime = reminderTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public LocalDate getLastRemindedOn() { return lastRemindedOn; }
    public void setLastRemindedOn(LocalDate lastRemindedOn) { this.lastRemindedOn = lastRemindedOn; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private String name;
        private String color;
        private HabitFrequency frequency = HabitFrequency.DAILY;
        private Set<DayOfWeek> weekdays;
        private LocalTime reminderTime;
        private int orderIndex = 0;

        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder color(String v) { this.color = v; return this; }
        public Builder frequency(HabitFrequency v) { this.frequency = v; return this; }
        public Builder weekdays(Set<DayOfWeek> v) { this.weekdays = v; return this; }
        public Builder reminderTime(LocalTime v) { this.reminderTime = v; return this; }
        public Builder orderIndex(int v) { this.orderIndex = v; return this; }

        public Habit build() {
            Habit o = new Habit();
            o.userId = this.userId;
            o.name = this.name;
            o.color = this.color;
            o.frequency = this.frequency != null ? this.frequency : HabitFrequency.DAILY;
            o.setWeekdaySet(this.weekdays);
            o.reminderTime = this.reminderTime;
            o.orderIndex = this.orderIndex;
            o.active = true;
            return o;
        }
    }
}
