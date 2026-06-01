package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.adapter.persistence.projection.CertificationDomainLookupProjection;
import com.certimaster.learning.domain.entity.CertificationDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationDomainReadRepository extends JpaRepository<CertificationDomain, Long> {

    @Query("SELECT d FROM CertificationDomain d WHERE " +
            "(:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CertificationDomain> search(@Param("keyword") String keyword, Pageable pageable);

    List<CertificationDomain> findByCertificationIdOrderByDisplayOrderAsc(Long certificationId);

    List<CertificationDomain> findByCertificationIdAndParentDomainIsNullOrderByDisplayOrderAsc(Long certificationId);

    @Query("""
            SELECT d.id AS id,
                   d.name AS name,
                   d.code AS code,
                   d.certification.id AS certificationId,
                   d.parentDomain.id AS parentDomainId,
                   d.displayOrder AS displayOrder,
                   d.status AS status
            FROM CertificationDomain d
            WHERE (:certificationId IS NULL OR d.certification.id = :certificationId)
              AND (d.status IS NULL OR d.status != 'DELETED')
              AND (:includeInactive = true OR d.status IS NULL OR d.status = 'ACTIVE')
            ORDER BY d.certification.name, d.displayOrder, d.name
            """)
    List<CertificationDomainLookupProjection> findLookupOptions(
            @Param("certificationId") Long certificationId,
            @Param("includeInactive") boolean includeInactive
    );
}
