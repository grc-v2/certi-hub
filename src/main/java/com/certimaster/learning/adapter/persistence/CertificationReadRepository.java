package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.adapter.persistence.projection.CertificationSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificationReadRepository extends JpaRepository<Certification, Long> {

    @Query("SELECT c FROM Certification c WHERE c.slug = :slug AND c.status != 'DELETED'")
    Optional<Certification> findActiveBySlug(@Param("slug") String slug);

    @Query("SELECT c FROM Certification c WHERE c.id = :id AND c.status != 'DELETED'")
    Optional<Certification> findActiveById(@Param("id") Long id);

    @Query("""
        SELECT c.id AS id,
               c.createdAt AS createdAt,
               c.updatedAt AS updatedAt,
               c.createdBy AS createdBy,
               c.updatedBy AS updatedBy,
               c.name AS name,
               c.code AS code,
               c.slug AS slug,
               c.tagline AS tagline,
               c.provider AS provider,
               c.level AS level,
               c.difficultyLevel AS difficultyLevel,
               c.description AS description,
               c.overview AS overview,
               c.durationMinutes AS durationMinutes,
               c.totalQuestions AS totalQuestions,
               c.passingScore AS passingScore,
               c.price AS price,
               c.examPriceUsd AS examPriceUsd,
               c.averagePrepWeeks AS averagePrepWeeks,
               c.status AS status
        FROM Certification c
        WHERE (:keyword IS NULL OR :keyword = ''
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.provider) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:provider IS NULL OR :provider = '' OR LOWER(c.provider) = LOWER(:provider))
        AND (:level IS NULL OR :level = '' OR c.level = :level)
        AND (:status IS NULL OR :status = '' OR c.status = :status)
        AND (:difficultyLevel IS NULL OR :difficultyLevel = '' OR c.difficultyLevel = :difficultyLevel)
        """)
    Page<CertificationSummaryProjection> searchSummary(
            @Param("keyword") String keyword,
            @Param("provider") String provider,
            @Param("level") String level,
            @Param("status") String status,
            @Param("difficultyLevel") String difficultyLevel,
            Pageable pageable
    );

    @Query(value = """
            SELECT id AS id,
                   created_at AS "createdAt",
                   updated_at AS "updatedAt",
                   created_by AS "createdBy",
                   updated_by AS "updatedBy",
                   name AS name,
                   code AS code,
                   slug AS slug,
                   tagline AS tagline,
                   provider AS provider,
                   level AS level,
                   difficulty_level AS "difficultyLevel",
                   description AS description,
                   overview AS overview,
                   duration_minutes AS "durationMinutes",
                   total_questions AS "totalQuestions",
                   passing_score AS "passingScore",
                   price AS price,
                   exam_price_usd AS "examPriceUsd",
                   average_prep_weeks AS "averagePrepWeeks",
                   status AS status
            FROM certifications
            WHERE search_vector @@ plainto_tsquery('english', :query)
              AND status != 'DELETED'
            ORDER BY ts_rank(search_vector, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = "SELECT count(*) FROM certifications WHERE search_vector @@ plainto_tsquery('english', :query) AND status != 'DELETED'",
            nativeQuery = true)
    Page<CertificationSummaryProjection> searchFullTextSummary(@Param("query") String query, Pageable pageable);
}
