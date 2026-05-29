package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ExamResultQueryRepository extends Repository<ExamResult, Long> {

    Optional<ExamResult> findById(Long id);

    @Query("""
            SELECT r FROM ExamResult r
            WHERE r.userId = :userId
            AND (:certificationId IS NULL OR r.certificationId = :certificationId)
            AND (:examId IS NULL OR r.examId = :examId)
            AND (:passStatus IS NULL OR r.passStatus = :passStatus)
            AND (:startDate IS NULL OR r.completedAt >= :startDate)
            AND (:endDate IS NULL OR r.completedAt <= :endDate)
            """)
    Page<ExamResult> searchWithDateRange(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            @Param("examId") Long examId,
            @Param("passStatus") String passStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM ExamResult r
            WHERE r.userId = :userId
            AND (:certificationId IS NULL OR r.certificationId = :certificationId)
            AND (:examId IS NULL OR r.examId = :examId)
            AND (:passStatus IS NULL OR r.passStatus = :passStatus)
            """)
    Page<ExamResult> search(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            @Param("examId") Long examId,
            @Param("passStatus") String passStatus,
            Pageable pageable
    );

    @Query(value = """
            SELECT COUNT(id),
                   SUM(CASE WHEN pass_status = 'PASSED' THEN 1 ELSE 0 END),
                   AVG(percentage),
                   MAX(percentage),
                   SUM(time_taken_seconds)
            FROM exam_results
            WHERE user_id = :userId
              AND (CAST(:certificationId AS BIGINT) IS NULL OR certification_id = :certificationId)
              AND (CAST(:examId AS BIGINT) IS NULL OR exam_id = :examId)
              AND (CAST(:passStatus AS TEXT) IS NULL OR pass_status = :passStatus)
              AND (CAST(:startDate AS TIMESTAMP) IS NULL OR completed_at >= :startDate)
              AND (CAST(:endDate AS TIMESTAMP) IS NULL OR completed_at <= :endDate)
            """, nativeQuery = true)
    List<Object[]> aggregateHistoryStats(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            @Param("examId") Long examId,
            @Param("passStatus") String passStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
            SELECT id,
                   CAST(RANK() OVER (
                       PARTITION BY COALESCE(exam_id::text, 'practice-' || id::text)
                       ORDER BY completed_at ASC
                   ) AS INTEGER) AS attempt_number
            FROM exam_results
            WHERE user_id = :userId
            """, nativeQuery = true)
    List<Object[]> findAttemptNumbersByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT certification_id, COUNT(id), AVG(percentage)
            FROM exam_results
            WHERE user_id = :userId
              AND (CAST(:certificationId AS BIGINT) IS NULL OR certification_id = :certificationId)
              AND (CAST(:examId AS BIGINT) IS NULL OR exam_id = :examId)
              AND (CAST(:passStatus AS TEXT) IS NULL OR pass_status = :passStatus)
              AND (CAST(:startDate AS TIMESTAMP) IS NULL OR completed_at >= :startDate)
              AND (CAST(:endDate AS TIMESTAMP) IS NULL OR completed_at <= :endDate)
            GROUP BY certification_id
            """, nativeQuery = true)
    List<Object[]> aggregateHistoryStatsByCertification(
            @Param("userId") Long userId,
            @Param("certificationId") Long certificationId,
            @Param("examId") Long examId,
            @Param("passStatus") String passStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
