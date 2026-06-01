package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.CourseLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseLessonProgressRepository extends JpaRepository<CourseLessonProgress, Long> {

    Optional<CourseLessonProgress> findByEnrollment_IdAndLesson_Id(Long enrollmentId, Long lessonId);

    long countByEnrollment_IdAndCompletedTrue(Long enrollmentId);

    @Query("""
            SELECT p.lesson.id FROM CourseLessonProgress p
            WHERE p.enrollment.userId = :userId
              AND p.lesson.module.course.id = :courseId
              AND p.completed = true
            """)
    List<Long> findCompletedLessonIds(@Param("userId") Long userId,
                                      @Param("courseId") Long courseId);
}
