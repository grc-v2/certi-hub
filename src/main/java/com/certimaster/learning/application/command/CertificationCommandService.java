package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.command.CreateCertificationCommand;
import com.certimaster.learning.application.command.DeleteCertificationCommand;
import com.certimaster.learning.application.command.UpdateCertificationCommand;
import com.certimaster.learning.application.command.UpdateCertificationStatusCommand;
import com.certimaster.learning.adapter.web.mapper.CertificationMapper;
import com.certimaster.learning.adapter.web.response.CertificationResponse;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CertificationCommandService {

    private static final Logger log = LoggerFactory.getLogger(CertificationCommandService.class);

    private static final String CERTIFICATION = "Certification";
    private static final String DELETED_STATUS = "DELETED";

    private final CertificationWriteRepository certificationRepository;
    private final CertificationMapper certificationMapper;

    public CertificationCommandService(CertificationWriteRepository certificationRepository,
                                       CertificationMapper certificationMapper) {
        this.certificationRepository = certificationRepository;
        this.certificationMapper = certificationMapper;
    }

    @CacheEvict(value = "certification-list", allEntries = true)
    public CertificationResponse create(CreateCertificationCommand command) {
        log.debug("Creating certification with code: {}", command.code());

        validateCodeUnique(command.code(), null);

        Certification certification = certificationMapper.toEntity(command);
        if (certification.getStatus() == null) {
            certification.setStatus("DRAFT");
        }

        Certification saved = certificationRepository.save(certification);
        log.info("Created certification with id: {}", saved.getId());

        return certificationMapper.toResponse(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "certification-list", allEntries = true),
            @CacheEvict(value = "certification-detail", allEntries = true)
    })
    public CertificationResponse update(UpdateCertificationCommand command) {
        log.debug("Updating certification with id: {}", command.id());

        Certification certification = findActiveOrThrow(command.id());
        validateCodeUnique(command.code(), command.id());

        certificationMapper.updateEntity(certification, command);
        Certification saved = certificationRepository.save(certification);
        log.info("Updated certification with id: {}", saved.getId());

        return certificationMapper.toResponse(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "certification-list", allEntries = true),
            @CacheEvict(value = "certification-detail", allEntries = true)
    })
    public void updateStatus(UpdateCertificationStatusCommand command) {
        log.debug("Updating status of certification with id: {} to {}", command.id(), command.status());

        Certification certification = findActiveOrThrow(command.id());
        certification.setStatus(command.status());
        certificationRepository.save(certification);

        log.info("Updated status of certification with id: {} to {}", command.id(), command.status());
    }

    @Caching(evict = {
            @CacheEvict(value = "certification-list", allEntries = true),
            @CacheEvict(value = "certification-detail", allEntries = true)
    })
    public void delete(DeleteCertificationCommand command) {
        log.debug("Soft deleting certification with id: {}", command.id());

        Certification certification = findActiveOrThrow(command.id());
        certification.setStatus(DELETED_STATUS);
        certificationRepository.save(certification);

        log.info("Soft deleted certification with id: {}", command.id());
    }

    private Certification findActiveOrThrow(Long id) {
        return certificationRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, id));
    }

    private void validateCodeUnique(String code, Long excludeId) {
        boolean exists = excludeId == null
                ? certificationRepository.existsByCodeIgnoreCase(code)
                : certificationRepository.existsByCodeIgnoreCaseAndIdNot(code, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(CERTIFICATION, code);
        }
    }
}
