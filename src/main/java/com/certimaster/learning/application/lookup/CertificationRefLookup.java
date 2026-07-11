package com.certimaster.learning.application.lookup;

import com.certimaster.learning.adapter.persistence.CertificationReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Minimal read-only lookup other modules may call to validate a certification
 * reference they store as a plain id (no cross-module FK). Lives in the
 * {@code lookup} named interface so the dependency stays legal for the modulith.
 */
@Service
@Transactional(readOnly = true)
public class CertificationRefLookup {

    private final CertificationReadRepository certificationRepository;

    public CertificationRefLookup(CertificationReadRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    /** True when the certification exists and is not soft-deleted (status != DELETED). */
    public boolean existsActiveById(Long certificationId) {
        return certificationId != null
                && certificationRepository.findActiveById(certificationId).isPresent();
    }
}
