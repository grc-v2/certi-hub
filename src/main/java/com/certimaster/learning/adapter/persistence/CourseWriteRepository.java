package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseWriteRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE c.id = :id AND c.published = true")
    Optional<Course> findPublishedById(@Param("id") Long id);
}
