package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionReadRepository extends JpaRepository<Question, Long> {

    @Query("""
            SELECT q FROM Question q
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(q.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(q.questionCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:domainId IS NULL OR q.domain.id = :domainId)
            AND (:certificationId IS NULL OR q.domain.certification.id = :certificationId)
            AND (:type IS NULL OR :type = '' OR q.type = :type)
            AND (:difficulty IS NULL OR :difficulty = '' OR q.difficulty = :difficulty)
            AND (:status IS NULL OR :status = '' OR q.status = :status)
            """)
    Page<Question> search(
            @Param("keyword") String keyword,
            @Param("domainId") Long domainId,
            @Param("certificationId") Long certificationId,
            @Param("type") String type,
            @Param("difficulty") String difficulty,
            @Param("status") String status,
            Pageable pageable
    );

    List<Question> findByDomainId(Long domainId);

    @Query("""
            SELECT q FROM Question q
            WHERE q.domain.id IN :domainIds
            AND q.status = :status
            """)
    List<Question> findByDomainIdInAndStatus(
            @Param("domainIds") List<Long> domainIds,
            @Param("status") String status
    );

    @Query("""
            SELECT q FROM Question q
            JOIN q.domain d
            WHERE d.certification.id = :certificationId
            AND q.status = 'PUBLISHED'
            """)
    List<Question> findPublishedByCertificationId(@Param("certificationId") Long certificationId);

    @Query("""
            SELECT q FROM Question q
            JOIN q.domain d
            WHERE d.certification.id = :certificationId
            AND q.status = 'PUBLISHED'
            AND (:difficulty IS NULL OR :difficulty = '' OR q.difficulty = :difficulty)
            AND (:domainIds IS NULL OR q.domain.id IN :domainIds)
            """)
    List<Question> findForSelection(
            @Param("certificationId") Long certificationId,
            @Param("difficulty") String difficulty,
            @Param("domainIds") List<Long> domainIds
    );
}
