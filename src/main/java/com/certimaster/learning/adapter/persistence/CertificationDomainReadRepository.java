package com.certimaster.learning.adapter.persistence;

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
}
