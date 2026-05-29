package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.application.command.CreateCertificationCommand;
import com.certimaster.learning.application.command.UpdateCertificationCommand;
import com.certimaster.learning.adapter.web.response.CertificationDetailResponse;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.adapter.web.response.CertificationResponse;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.domain.entity.Exam;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CertificationMapper {

    private final CertificationDomainMapper domainMapper;

    public CertificationMapper(CertificationDomainMapper domainMapper) {
        this.domainMapper = domainMapper;
    }

    public CertificationResponse toResponse(Certification certification) {
        if (certification == null) {
            return null;
        }
        return CertificationResponse.builder()
                .id(certification.getId())
                .createdAt(certification.getCreatedAt())
                .updatedAt(certification.getUpdatedAt())
                .createdBy(certification.getCreatedBy())
                .updatedBy(certification.getUpdatedBy())
                .name(certification.getName())
                .code(certification.getCode())
                .slug(certification.getSlug())
                .tagline(certification.getTagline())
                .provider(certification.getProvider())
                .description(certification.getDescription())
                .overview(certification.getOverview())
                .level(certification.getLevel())
                .difficultyLevel(certification.getDifficultyLevel())
                .durationMinutes(certification.getDurationMinutes())
                .passingScore(certification.getPassingScore())
                .totalQuestions(certification.getTotalQuestions())
                .price(certification.getPrice())
                .examPriceUsd(certification.getExamPriceUsd())
                .averagePrepWeeks(certification.getAveragePrepWeeks())
                .status(certification.getStatus())
                .build();
    }

    public CertificationDetailResponse toDetailResponse(Certification certification) {
        if (certification == null) {
            return null;
        }

        List<CertificationDomainResponse> domains = mapDomains(certification);
        List<ExamResponse> exams = mapExams(certification);

        return CertificationDetailResponse.detailBuilder()
                .id(certification.getId())
                .createdAt(certification.getCreatedAt())
                .updatedAt(certification.getUpdatedAt())
                .createdBy(certification.getCreatedBy())
                .updatedBy(certification.getUpdatedBy())
                .name(certification.getName())
                .code(certification.getCode())
                .slug(certification.getSlug())
                .tagline(certification.getTagline())
                .provider(certification.getProvider())
                .description(certification.getDescription())
                .overview(certification.getOverview())
                .level(certification.getLevel())
                .difficultyLevel(certification.getDifficultyLevel())
                .durationMinutes(certification.getDurationMinutes())
                .passingScore(certification.getPassingScore())
                .totalQuestions(certification.getTotalQuestions())
                .price(certification.getPrice())
                .examPriceUsd(certification.getExamPriceUsd())
                .averagePrepWeeks(certification.getAveragePrepWeeks())
                .status(certification.getStatus())
                .domains(domains)
                .exams(exams)
                .build();
    }

    public Certification toEntity(CreateCertificationCommand command) {
        if (command == null) {
            return null;
        }
        return Certification.builder()
                .name(command.name())
                .code(command.code())
                .slug(command.code() != null ? command.code().toLowerCase().replace("_", "-") : null)
                .provider(command.provider())
                .description(command.description())
                .level(command.level())
                .durationMinutes(command.durationMinutes())
                .passingScore(command.passingScore())
                .totalQuestions(command.totalQuestions())
                .price(command.price())
                .status(command.status())
                .build();
    }

    public void updateEntity(Certification certification, UpdateCertificationCommand command) {
        if (certification == null || command == null) {
            return;
        }
        if (command.name() != null) {
            certification.setName(command.name());
        }
        if (command.code() != null) {
            certification.setCode(command.code());
            certification.setSlug(command.code().toLowerCase().replace("_", "-"));
        }
        if (command.provider() != null) {
            certification.setProvider(command.provider());
        }
        if (command.description() != null) {
            certification.setDescription(command.description());
        }
        if (command.level() != null) {
            certification.setLevel(command.level());
        }
        if (command.durationMinutes() != null) {
            certification.setDurationMinutes(command.durationMinutes());
        }
        if (command.passingScore() != null) {
            certification.setPassingScore(command.passingScore());
        }
        if (command.totalQuestions() != null) {
            certification.setTotalQuestions(command.totalQuestions());
        }
        if (command.price() != null) {
            certification.setPrice(command.price());
        }
        if (command.status() != null) {
            certification.setStatus(command.status());
        }
    }

    private List<CertificationDomainResponse> mapDomains(Certification certification) {
        if (certification.getDomains() == null || certification.getDomains().isEmpty()) {
            return Collections.emptyList();
        }
        return certification.getDomains().stream()
                .filter(Objects::nonNull)
                .map(domainMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<ExamResponse> mapExams(Certification certification) {
        if (certification.getExams() == null || certification.getExams().isEmpty()) {
            return Collections.emptyList();
        }
        return certification.getExams().stream()
                .filter(Objects::nonNull)
                .map(this::mapExamToResponse)
                .collect(Collectors.toList());
    }

    private ExamResponse mapExamToResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .createdBy(exam.getCreatedBy())
                .updatedBy(exam.getUpdatedBy())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .title(exam.getTitle())
                .type(exam.getType())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .passingScore(exam.getPassingScore())
                .status(exam.getStatus())
                .certificationId(exam.getCertification() != null ? exam.getCertification().getId() : null)
                .certificationName(exam.getCertification() != null ? exam.getCertification().getName() : null)
                .build();
    }
}
