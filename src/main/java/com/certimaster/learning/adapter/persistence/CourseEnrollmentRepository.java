package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    @Query("""
            SELECT e FROM CourseEnrollment e
            JOIN FETCH e.course c
            LEFT JOIN FETCH c.certification
            WHERE e.userId = :userId
              AND c.id = :courseId
            """)
    Optional<CourseEnrollment> findByUserIdAndCourseIdWithCourse(@Param("userId") Long userId,
                                                                 @Param("courseId") Long courseId);

    Optional<CourseEnrollment> findByUserIdAndCourse_Id(Long userId, Long courseId);

    @Query("""
            SELECT e FROM CourseEnrollment e
            JOIN FETCH e.course c
            LEFT JOIN FETCH c.certification
            WHERE e.userId = :userId
            ORDER BY e.lastAccessedAt DESC NULLS LAST, e.enrolledAt DESC
            """)
    List<CourseEnrollment> findByUserIdWithCourse(@Param("userId") Long userId);
}
