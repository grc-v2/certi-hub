package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    @Query("""
            SELECT m FROM CourseModule m
            JOIN FETCH m.course c
            WHERE m.id = :moduleId AND c.id = :courseId
            """)
    Optional<CourseModule> findByIdAndCourseId(@Param("courseId") Long courseId,
                                               @Param("moduleId") Long moduleId);

    @Query("SELECT COALESCE(MAX(m.orderIndex), 0) FROM CourseModule m WHERE m.course.id = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);
}
