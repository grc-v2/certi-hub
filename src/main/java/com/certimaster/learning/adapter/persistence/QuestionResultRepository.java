package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.QuestionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionResultRepository extends JpaRepository<QuestionResult, Long> {

    /**
     * Find question results by exam result ID.
     */
    List<QuestionResult> findByExamResultId(Long resultId);

    /**
     * Find question results by exam result ID with pagination.
     */
    Page<QuestionResult> findByExamResultId(Long resultId, Pageable pageable);

    /**
     * Search question results with multiple filters.
     */
    @Query("""
            SELECT qr FROM QuestionResult qr
            WHERE qr.examResult.id = :resultId
            AND (:isCorrect IS NULL OR qr.isCorrect = :isCorrect)
            AND (:topicId IS NULL OR qr.topicId = :topicId)
            AND (:difficulty IS NULL OR :difficulty = '' OR qr.difficulty = :difficulty)
            AND (:wasFlagged IS NULL OR qr.wasFlagged = :wasFlagged)
            """)
    Page<QuestionResult> searchByFilters(
            @Param("resultId") Long resultId,
            @Param("isCorrect") Boolean isCorrect,
            @Param("topicId") Long topicId,
            @Param("difficulty") String difficulty,
            @Param("wasFlagged") Boolean wasFlagged,
            Pageable pageable
    );

    /**
     * Count questions by difficulty for an exam result.
     */
    @Query("""
            SELECT qr.difficulty, COUNT(qr), SUM(CASE WHEN qr.isCorrect = true THEN 1 ELSE 0 END)
            FROM QuestionResult qr
            WHERE qr.examResult.id = :resultId
            GROUP BY qr.difficulty
            """)
    List<Object[]> countByDifficultyForResult(@Param("resultId") Long resultId);
}
