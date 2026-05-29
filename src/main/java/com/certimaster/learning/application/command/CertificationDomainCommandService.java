package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.command.CreateCertificationDomainCommand;
import com.certimaster.learning.application.command.DeleteCertificationDomainCommand;
import com.certimaster.learning.application.command.UpdateCertificationDomainCommand;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.adapter.web.mapper.CertificationDomainMapper;
import com.certimaster.learning.adapter.persistence.CertificationDomainWriteRepository;
import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CertificationDomainCommandService {

    private static final String DOMAIN = "CertificationDomain";
    private static final String CERTIFICATION = "Certification";

    private final CertificationDomainWriteRepository certificationDomainRepository;
    private final CertificationWriteRepository certificationRepository;
    private final CertificationDomainMapper certificationDomainMapper;

    public CertificationDomainCommandService(CertificationDomainWriteRepository certificationDomainRepository,
                                             CertificationWriteRepository certificationRepository,
                                             CertificationDomainMapper certificationDomainMapper) {
        this.certificationDomainRepository = certificationDomainRepository;
        this.certificationRepository = certificationRepository;
        this.certificationDomainMapper = certificationDomainMapper;
    }

    public CertificationDomainResponse create(CreateCertificationDomainCommand command) {
        if (certificationDomainRepository.existsByNameAndCertificationId(command.name(), command.certificationId())) {
            throw BusinessException.duplicateResource(DOMAIN, command.name());
        }

        Certification certification = certificationRepository.findById(command.certificationId())
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, command.certificationId()));

        CertificationDomain domain = certificationDomainMapper.toEntity(command);
        domain.setCertification(certification);

        if (command.parentDomainId() != null) {
            CertificationDomain parentDomain = findOrThrow(command.parentDomainId());
            domain.setParentDomain(parentDomain);
        }

        CertificationDomain saved = certificationDomainRepository.save(domain);
        return certificationDomainMapper.toResponse(saved);
    }

    public CertificationDomainResponse update(UpdateCertificationDomainCommand command) {
        CertificationDomain domain = findOrThrow(command.id());

        if (certificationDomainRepository.existsByNameAndCertificationIdAndIdNot(
                command.name(), domain.getCertification().getId(), command.id())) {
            throw BusinessException.duplicateResource(DOMAIN, command.name());
        }

        certificationDomainMapper.updateEntity(domain, command);

        if (command.parentDomainId() != null) {
            CertificationDomain parentDomain = findOrThrow(command.parentDomainId());
            domain.setParentDomain(parentDomain);
        } else {
            domain.setParentDomain(null);
        }

        CertificationDomain saved = certificationDomainRepository.save(domain);
        return certificationDomainMapper.toResponse(saved);
    }

    public void delete(DeleteCertificationDomainCommand command) {
        CertificationDomain domain = findOrThrow(command.id());
        certificationDomainRepository.delete(domain);
    }

    private CertificationDomain findOrThrow(Long id) {
        return certificationDomainRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(DOMAIN, id));
    }
}
