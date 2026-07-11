package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.PomodoroSession;
import com.certimaster.productivity.domain.entity.PomodoroSessionKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, Long> {

    Page<PomodoroSession> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    Page<PomodoroSession> findByUserIdAndTaskIdOrderByStartedAtDesc(Long userId, Long taskId, Pageable pageable);

    List<PomodoroSession> findByUserIdAndStartedAtGreaterThanEqual(Long userId, LocalDateTime from);

    long countByUserIdAndTaskIdAndKindAndCompletedTrue(Long userId, Long taskId, PomodoroSessionKind kind);
}
