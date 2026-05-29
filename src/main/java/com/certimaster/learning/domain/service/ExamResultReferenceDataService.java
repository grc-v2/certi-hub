package com.certimaster.learning.domain.service;

import com.certimaster.learning.application.query.GetCertificationByIdQuery;
import com.certimaster.learning.application.query.CertificationQueryService;
import com.certimaster.learning.application.query.GetExamByIdQuery;
import com.certimaster.learning.application.query.ExamQueryService;
import com.certimaster.learning.adapter.web.response.CertificationInfo;
import com.certimaster.learning.adapter.web.response.ExamInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ExamResultReferenceDataService {

    private static final Logger log = LoggerFactory.getLogger(ExamResultReferenceDataService.class);

    private final ExamQueryService examQueryService;
    private final CertificationQueryService certificationQueryService;

    public ExamResultReferenceDataService(ExamQueryService examQueryService,
                                          CertificationQueryService certificationQueryService) {
        this.examQueryService = examQueryService;
        this.certificationQueryService = certificationQueryService;
    }

    public Optional<ExamInfo> getExam(Long examId) {
        if (examId == null) {
            log.warn("getExam called with null examId");
            return Optional.empty();
        }
        try {
            log.info("Fetching exam directly from ExamQueryService: {}", examId);
            var response = examQueryService.getById(new GetExamByIdQuery(examId));
            if (response != null) {
                ExamInfo info = ExamInfo.builder()
                        .id(response.getId())
                        .title(response.getTitle())
                        .type(response.getType())
                        .description(response.getDescription())
                        .durationMinutes(response.getDurationMinutes())
                        .totalQuestions(response.getTotalQuestions())
                        .passingScore(response.getPassingScore())
                        .status(response.getStatus())
                        .certificationId(response.getCertificationId())
                        .certificationName(response.getCertificationName())
                        .build();
                log.info("Successfully fetched exam {}: title={}", examId, info.getTitle());
                return Optional.of(info);
            }
        } catch (Exception e) {
            log.error("Failed to fetch exam {}: {} - {}", examId, e.getClass().getSimpleName(), e.getMessage());
        }
        return Optional.empty();
    }

    public Map<Long, ExamInfo> getExams(Set<Long> examIds) {
        if (examIds == null || examIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, ExamInfo> result = new HashMap<>();
        for (Long examId : examIds) {
            getExam(examId).ifPresent(info -> result.put(examId, info));
        }
        log.debug("Fetched {} exams out of {} requested", result.size(), examIds.size());
        return result;
    }

    public Optional<CertificationInfo> getCertification(Long certificationId) {
        if (certificationId == null) {
            log.warn("getCertification called with null certificationId");
            return Optional.empty();
        }
        try {
            log.info("Fetching certification directly from CertificationQueryService: {}", certificationId);
            var response = certificationQueryService.getById(new GetCertificationByIdQuery(certificationId));
            if (response != null) {
                CertificationInfo info = CertificationInfo.builder()
                        .id(response.getId())
                        .name(response.getName())
                        .code(response.getCode())
                        .provider(response.getProvider())
                        .level(response.getLevel())
                        .description(response.getDescription())
                        .durationMinutes(response.getDurationMinutes())
                        .totalQuestions(response.getTotalQuestions())
                        .passingScore(response.getPassingScore())
                        .status(response.getStatus())
                        .build();
                log.info("Successfully fetched certification {}: name={}", certificationId, info.getName());
                return Optional.of(info);
            }
        } catch (Exception e) {
            log.error("Failed to fetch certification {}: {} - {}", certificationId, e.getClass().getSimpleName(), e.getMessage());
        }
        return Optional.empty();
    }

    public Map<Long, CertificationInfo> getCertifications(Set<Long> certificationIds) {
        if (certificationIds == null || certificationIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, CertificationInfo> result = new HashMap<>();
        for (Long certId : certificationIds) {
            getCertification(certId).ifPresent(info -> result.put(certId, info));
        }
        log.debug("Fetched {} certifications out of {} requested", result.size(), certificationIds.size());
        return result;
    }
}
