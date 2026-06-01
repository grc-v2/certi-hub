package com.certimaster.learning.application.lookup;

import com.certimaster.learning.adapter.persistence.CertificationDomainReadRepository;
import com.certimaster.learning.adapter.persistence.CertificationReadRepository;
import com.certimaster.learning.adapter.persistence.TagReadRepository;
import com.certimaster.learning.adapter.persistence.projection.CertificationDomainLookupProjection;
import com.certimaster.learning.adapter.persistence.projection.CertificationLookupProjection;
import com.certimaster.learning.domain.entity.CertificationStatus;
import com.certimaster.learning.domain.entity.ProviderStatus;
import com.certimaster.learning.domain.entity.QuestionBankStatus;
import com.certimaster.learning.domain.entity.QuestionStatus;
import com.certimaster.learning.domain.entity.RelationType;
import com.certimaster.learning.domain.entity.ReportStatus;
import com.certimaster.learning.domain.entity.ReportType;
import com.certimaster.learning.domain.entity.SessionMode;
import com.certimaster.learning.domain.entity.SessionStatus;
import com.certimaster.learning.domain.entity.Tag;
import com.certimaster.shared.constant.Status;
import com.certimaster.shared.dto.LookupOption;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class LearningLookupService {

    private static final List<String> CERTIFICATION_LEVELS = List.of("BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT");
    private static final List<String> EXAM_TYPES = List.of("PRACTICE", "MOCK", "FINAL", "DIAGNOSTIC", "TOPIC_WISE");
    private static final List<String> EXAM_STATUSES = List.of("ACTIVE", "INACTIVE", "DRAFT");
    private static final List<String> QUESTION_TYPES = List.of("SINGLE_CHOICE", "MULTIPLE_CHOICE", "TRUE_FALSE");
    private static final List<String> QUESTION_DIFFICULTIES = List.of("EASY", "MEDIUM", "HARD");
    private static final List<String> RESULT_STATUSES = List.of("PASSED", "FAILED");

    private final CertificationReadRepository certificationRepository;
    private final CertificationDomainReadRepository certificationDomainRepository;
    private final TagReadRepository tagRepository;

    public LearningLookupService(CertificationReadRepository certificationRepository,
                                 CertificationDomainReadRepository certificationDomainRepository,
                                 TagReadRepository tagRepository) {
        this.certificationRepository = certificationRepository;
        this.certificationDomainRepository = certificationDomainRepository;
        this.tagRepository = tagRepository;
    }

    public Map<String, List<LookupOption>> constants() {
        Map<String, List<LookupOption>> constants = new LinkedHashMap<>();
        constants.put("commonStatuses", enumOptions(Status.class));
        constants.put("certificationStatuses", enumOptions(CertificationStatus.class));
        constants.put("certificationLevels", valueOptions(CERTIFICATION_LEVELS));
        constants.put("certificationDifficultyLevels", enumOptions(com.certimaster.learning.domain.entity.DifficultyLevel.class));
        constants.put("providerStatuses", enumOptions(ProviderStatus.class));
        constants.put("examTypes", valueOptions(EXAM_TYPES));
        constants.put("examStatuses", valueOptions(EXAM_STATUSES));
        constants.put("questionTypes", valueOptions(QUESTION_TYPES));
        constants.put("questionDifficulties", valueOptions(QUESTION_DIFFICULTIES));
        constants.put("questionStatuses", enumOptions(QuestionStatus.class));
        constants.put("questionBankStatuses", enumOptions(QuestionBankStatus.class));
        constants.put("sessionModes", enumOptions(SessionMode.class));
        constants.put("sessionStatuses", enumOptions(SessionStatus.class));
        constants.put("resultStatuses", valueOptions(RESULT_STATUSES));
        constants.put("reportTypes", enumOptions(ReportType.class));
        constants.put("reportStatuses", enumOptions(ReportStatus.class));
        constants.put("relationTypes", enumOptions(RelationType.class));
        return constants;
    }

    public Map<String, List<LookupOption>> catalogs(boolean includeInactive, Long certificationId) {
        List<CertificationLookupProjection> certifications = certificationRepository.findLookupOptions(includeInactive);

        Map<String, List<LookupOption>> catalogs = new LinkedHashMap<>();
        catalogs.put("providers", providerOptions(certifications));
        catalogs.put("certifications", certificationOptions(certifications));
        catalogs.put("certificationDomains", certificationDomainOptions(certificationId, includeInactive));
        catalogs.put("questionTags", questionTagOptions(includeInactive));
        return catalogs;
    }

    private List<LookupOption> certificationOptions(List<CertificationLookupProjection> certifications) {
        return certifications.stream()
                .map(certification -> LookupOption.of(
                        certification.getId().toString(),
                        certification.getName(),
                        metadata(
                                "id", certification.getId(),
                                "code", certification.getCode(),
                                "slug", certification.getSlug(),
                                "provider", providerLabel(certification),
                                "status", certification.getStatus()
                        )
                ))
                .toList();
    }

    private List<LookupOption> providerOptions(List<CertificationLookupProjection> certifications) {
        Map<String, LookupOption> providers = new LinkedHashMap<>();
        for (CertificationLookupProjection certification : certifications) {
            String provider = providerLabel(certification);
            if (provider == null || provider.isBlank()) {
                continue;
            }
            providers.putIfAbsent(provider.toLowerCase(), LookupOption.of(
                    provider,
                    provider,
                    metadata("logo", certification.getProviderLogo())
            ));
        }
        return providers.values().stream()
                .sorted(Comparator.comparing(LookupOption::label, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<LookupOption> certificationDomainOptions(Long certificationId, boolean includeInactive) {
        return certificationDomainRepository.findLookupOptions(certificationId, includeInactive).stream()
                .map(domain -> LookupOption.of(
                        domain.getId().toString(),
                        domain.getName(),
                        metadata(
                                "id", domain.getId(),
                                "code", domain.getCode(),
                                "certificationId", domain.getCertificationId(),
                                "parentDomainId", domain.getParentDomainId(),
                                "displayOrder", domain.getDisplayOrder(),
                                "status", domain.getStatus()
                        )
                ))
                .toList();
    }

    private List<LookupOption> questionTagOptions(boolean includeInactive) {
        return tagRepository.findLookupOptions(includeInactive).stream()
                .sorted(Comparator.comparing(Tag::getName, String.CASE_INSENSITIVE_ORDER))
                .map(tag -> LookupOption.of(
                        tag.getId().toString(),
                        tag.getName(),
                        metadata(
                                "id", tag.getId(),
                                "status", tag.getStatus()
                        )
                ))
                .toList();
    }

    private String providerLabel(CertificationLookupProjection certification) {
        if (certification.getProviderName() != null && !certification.getProviderName().isBlank()) {
            return certification.getProviderName();
        }
        return certification.getProvider();
    }

    private <E extends Enum<E>> List<LookupOption> enumOptions(Class<E> enumClass) {
        return valueOptions(java.util.Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toList());
    }

    private List<LookupOption> valueOptions(List<String> values) {
        return values.stream()
                .map(value -> LookupOption.of(value, toLabel(value)))
                .toList();
    }

    private String toLabel(String value) {
        String[] parts = value.toLowerCase().split("_");
        StringBuilder label = new StringBuilder();
        for (String part : parts) {
            if (!label.isEmpty()) {
                label.append(' ');
            }
            label.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return label.toString();
    }

    private Map<String, Object> metadata(Object... keyValues) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            Object value = keyValues[i + 1];
            if (value != null) {
                metadata.put((String) keyValues[i], value);
            }
        }
        return metadata;
    }
}
