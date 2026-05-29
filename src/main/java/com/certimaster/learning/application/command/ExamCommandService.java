package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import com.certimaster.learning.application.command.CreateExamCommand;
import com.certimaster.learning.application.command.DeleteExamCommand;
import com.certimaster.learning.application.command.UpdateExamCommand;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.adapter.web.mapper.ExamMapper;
import com.certimaster.learning.adapter.persistence.ExamWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExamCommandService {

    private static final Logger log = LoggerFactory.getLogger(ExamCommandService.class);

    private static final String EXAM = "Exam";
    private static final String CERTIFICATION = "Certification";
    private static final String DELETED_STATUS = "DELETED";

    private final ExamWriteRepository examRepository;
    private final CertificationWriteRepository certificationRepository;
    private final ExamMapper examMapper;

    public ExamCommandService(ExamWriteRepository examRepository,
                              CertificationWriteRepository certificationRepository,
                              ExamMapper examMapper) {
        this.examRepository = examRepository;
        this.certificationRepository = certificationRepository;
        this.examMapper = examMapper;
    }

    public ExamResponse create(CreateExamCommand command) {
        log.debug("Creating exam with title: {}", command.title());

        Certification certification = findCertificationOrThrow(command.certificationId());
        validateTitleUnique(command.title(), command.certificationId(), null);

        Exam exam = examMapper.toEntity(command);
        exam.setCertification(certification);
        if (exam.getStatus() == null) {
            exam.setStatus("DRAFT");
        }

        Exam saved = examRepository.save(exam);
        log.info("Created exam with id: {}", saved.getId());

        return examMapper.toResponse(saved);
    }

    public ExamResponse update(UpdateExamCommand command) {
        log.debug("Updating exam with id: {}", command.id());

        Exam exam = findActiveOrThrow(command.id());

        if (!exam.getCertification().getId().equals(command.certificationId())) {
            Certification certification = findCertificationOrThrow(command.certificationId());
            exam.setCertification(certification);
        }

        validateTitleUnique(command.title(), command.certificationId(), command.id());
        examMapper.updateEntity(exam, command);

        Exam saved = examRepository.save(exam);
        log.info("Updated exam with id: {}", saved.getId());

        return examMapper.toResponse(saved);
    }

    public void delete(DeleteExamCommand command) {
        log.debug("Soft deleting exam with id: {}", command.id());

        Exam exam = findActiveOrThrow(command.id());
        exam.setStatus(DELETED_STATUS);
        examRepository.save(exam);

        log.info("Soft deleted exam with id: {}", command.id());
    }

    private Exam findActiveOrThrow(Long id) {
        return examRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, id));
    }

    private Certification findCertificationOrThrow(Long certificationId) {
        return certificationRepository.findActiveById(certificationId)
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, certificationId));
    }

    private void validateTitleUnique(String title, Long certificationId, Long excludeId) {
        boolean exists = excludeId == null
                ? examRepository.existsByTitleIgnoreCaseAndCertificationId(title, certificationId)
                : examRepository.existsByTitleIgnoreCaseAndCertificationIdAndIdNot(title, certificationId, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(EXAM, title);
        }
    }
}
