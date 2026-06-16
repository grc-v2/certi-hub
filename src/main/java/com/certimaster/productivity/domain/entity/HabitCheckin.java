package com.certimaster.productivity.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

/**
 * A confirmation that a {@link Habit} was performed on a given day. At most one per
 * (habit, date). The presence of a row is the "done" signal; un-confirming deletes it.
 */
@Entity
@Table(name = "habit_checkins", uniqueConstraints = {
    @UniqueConstraint(name = "uk_habit_checkins_habit_date", columnNames = {"habit_id", "checkin_date"})
})
public class HabitCheckin extends BaseEntity {

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    public HabitCheckin() {
        super();
    }

    public HabitCheckin(Long habitId, Long userId, LocalDate checkinDate) {
        this.habitId = habitId;
        this.userId = userId;
        this.checkinDate = checkinDate;
    }

    public Long getHabitId() { return habitId; }
    public void setHabitId(Long habitId) { this.habitId = habitId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }
}
