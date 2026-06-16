package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.HabitCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitCheckinRepository extends JpaRepository<HabitCheckin, Long> {

    boolean existsByHabitIdAndCheckinDate(Long habitId, LocalDate checkinDate);

    Optional<HabitCheckin> findByHabitIdAndCheckinDate(Long habitId, LocalDate checkinDate);

    List<HabitCheckin> findByHabitIdAndCheckinDateBetween(Long habitId, LocalDate from, LocalDate to);

    /** All of a user's check-ins on/after a date — used to compute today's status and streaks. */
    List<HabitCheckin> findByUserIdAndCheckinDateGreaterThanEqual(Long userId, LocalDate from);

    void deleteByHabitId(Long habitId);
}
