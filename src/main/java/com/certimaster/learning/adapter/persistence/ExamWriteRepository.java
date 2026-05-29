package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamWriteRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e WHERE e.id = :id AND e.status != 'DELETED'")
    Optional<Exam> findActiveById(@Param("id") Long id);

    boolean existsByTitleIgnoreCaseAndCertificationIdAndIdNot(String title, Long certificationId, Long id);

    boolean existsByTitleIgnoreCaseAndCertificationId(String title, Long certificationId);
}
