package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, Long> {

    @Query("""
            SELECT l FROM CourseLesson l
            JOIN FETCH l.module m
            JOIN FETCH m.course c
            WHERE l.id = :lessonId
              AND c.id = :courseId
              AND c.published = true
            """)
    Optional<CourseLesson> findPublishedCourseLesson(@Param("courseId") Long courseId,
                                                     @Param("lessonId") Long lessonId);

    @Query("""
            SELECT l.id FROM CourseLesson l
            JOIN l.module m
            WHERE m.course.id = :courseId
            ORDER BY m.orderIndex ASC, l.orderIndex ASC, l.id ASC
            """)
    List<Long> findOrderedLessonIdsByCourseId(@Param("courseId") Long courseId);

    long countByModule_Course_Id(Long courseId);
}
