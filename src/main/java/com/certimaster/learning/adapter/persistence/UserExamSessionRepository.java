package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.UserExamSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserExamSessionRepository extends JpaRepository<UserExamSession, Long> {

    Optional<UserExamSession> findByUserIdAndExam_IdAndStatus(Long userId, Long examId, String status);

    List<UserExamSession> findByUserIdAndStatus(Long userId, String status);

    Optional<UserExamSession> findByIdAndUserId(Long id, Long userId);

    int countByUserIdAndStatus(Long userId, String status);

    /**
     * Issue 7: Find sessions that are still IN_PROGRESS but have exceeded their allotted duration.
     * Used by the auto-expire scheduler.
     */
    @Query(value = """
            SELECT * FROM user_exam_sessions
            WHERE status = 'IN_PROGRESS'
              AND duration_minutes IS NOT NULL
              AND start_time + (duration_minutes * INTERVAL '1 minute') <= :now
            """, nativeQuery = true)
    List<UserExamSession> findExpiredSessions(@Param("now") LocalDateTime now);

    @Query("""
            SELECT s FROM UserExamSession s
            WHERE s.userId = :userId
            AND (:certificationId IS NULL OR s.certification.id = :certificationId)
            AND s.status IN ('COMPLETED', 'TIMED_OUT')
            ORDER BY s.endTime DESC
            """)
    Page<UserExamSession> findCompletedByUserIdAndCertificationId(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            Pageable pageable);

    @Query("""
            SELECT s FROM UserExamSession s
            WHERE s.userId = :userId
            AND s.certification.id = :certificationId
            AND s.status IN ('COMPLETED', 'TIMED_OUT')
            ORDER BY s.endTime ASC
            """)
    List<UserExamSession> findCompletedByUserIdAndCertificationIdOrderByEndTimeAsc(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId);

}
