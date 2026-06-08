package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseReadRepository extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = {"certification"})
    @Query("""
            SELECT c FROM Course c
            WHERE c.published = true
              AND (:certificationId IS NULL OR c.certification.id = :certificationId)
              AND (:search IS NULL OR :search = ''
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.instructor) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.certification.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Course> searchPublished(@Param("certificationId") Long certificationId,
                                 @Param("search") String search,
                                 Pageable pageable);

    @Query("""
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.certification
            LEFT JOIN FETCH c.modules m
            LEFT JOIN FETCH m.lessons
            WHERE c.id = :id
              AND c.published = true
            """)
    Optional<Course> findPublishedDetailById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"certification"})
    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.published = true")
    Optional<Course> findPublishedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"certification"})
    @Query("""
            SELECT c FROM Course c
            WHERE (:certificationId IS NULL OR c.certification.id = :certificationId)
              AND (:published IS NULL OR c.published = :published)
              AND (:search IS NULL OR :search = ''
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.instructor) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.certification.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Course> searchAdmin(@Param("certificationId") Long certificationId,
                             @Param("search") String search,
                             @Param("published") Boolean published,
                             Pageable pageable);

    @Query("""
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.certification
            LEFT JOIN FETCH c.modules m
            LEFT JOIN FETCH m.lessons
            WHERE c.id = :id
            """)
    Optional<Course> findDetailById(@Param("id") Long id);
}
