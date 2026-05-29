package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamReadRepository extends JpaRepository<Exam, Long> {

    @Query("""
            SELECT e FROM Exam e
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:certificationId IS NULL OR e.certification.id = :certificationId)
            AND (:type IS NULL OR :type = '' OR e.type = :type)
            AND (:status IS NULL OR :status = '' OR e.status = :status)
            """)
    Page<Exam> search(
            @Param("keyword") String keyword,
            @Param("certificationId") Long certificationId,
            @Param("type") String type,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT e FROM Exam e WHERE e.id = :id AND e.status != 'DELETED'")
    Optional<Exam> findActiveById(@Param("id") Long id);

    List<Exam> findByCertificationId(Long certificationId);
}
