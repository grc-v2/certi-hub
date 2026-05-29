package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

    @Query("SELECT eq FROM ExamQuestion eq " +
           "JOIN FETCH eq.question q " +
           "JOIN FETCH q.domain d " +
           "LEFT JOIN FETCH q.questionOptions " +
           "WHERE eq.exam.id = :examId " +
           "ORDER BY eq.orderIndex ASC")
    List<ExamQuestion> findByExamIdWithDetails(@Param("examId") Long examId);
}
