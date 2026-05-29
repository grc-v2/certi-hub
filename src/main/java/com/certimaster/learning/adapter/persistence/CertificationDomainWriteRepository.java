package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.CertificationDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationDomainWriteRepository extends JpaRepository<CertificationDomain, Long> {

    boolean existsByNameAndCertificationId(String name, Long certificationId);

    boolean existsByNameAndCertificationIdAndIdNot(String name, Long certificationId, Long id);
}
