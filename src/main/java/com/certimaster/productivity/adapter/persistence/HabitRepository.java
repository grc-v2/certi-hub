package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByUserIdOrderByOrderIndexAscCreatedAtAsc(Long userId);

    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    /** Active habits with a reminder configured — the working set for the reminder scheduler. */
    List<Habit> findByActiveTrueAndReminderTimeIsNotNull();
}
