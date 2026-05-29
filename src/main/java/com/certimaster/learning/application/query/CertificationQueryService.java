package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.query.GetCertificationByIdQuery;
import com.certimaster.learning.application.query.GetCertificationBySlugQuery;
import com.certimaster.learning.application.query.GetCertificationDomainTreeBySlugQuery;
import com.certimaster.learning.application.query.SearchCertificationsFullTextQuery;
import com.certimaster.learning.application.query.SearchCertificationsQuery;
import com.certimaster.learning.adapter.web.mapper.CertificationDomainMapper;
import com.certimaster.learning.adapter.web.mapper.CertificationMapper;
import com.certimaster.learning.adapter.web.response.CertificationDetailResponse;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.adapter.web.response.CertificationResponse;
import com.certimaster.learning.adapter.persistence.projection.CertificationSummaryProjection;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.adapter.persistence.CertificationDomainReadRepository;
import com.certimaster.learning.adapter.persistence.CertificationReadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CertificationQueryService {

    private static final Logger log = LoggerFactory.getLogger(CertificationQueryService.class);

    private static final String CERTIFICATION = "Certification";

    private final CertificationReadRepository certificationRepository;
    private final CertificationMapper certificationMapper;
    private final CertificationDomainReadRepository certificationDomainRepository;
    private final CertificationDomainMapper certificationDomainMapper;

    public CertificationQueryService(CertificationReadRepository certificationRepository,
                                     CertificationMapper certificationMapper,
                                     CertificationDomainReadRepository certificationDomainRepository,
                                     CertificationDomainMapper certificationDomainMapper) {
        this.certificationRepository = certificationRepository;
        this.certificationMapper = certificationMapper;
        this.certificationDomainRepository = certificationDomainRepository;
        this.certificationDomainMapper = certificationDomainMapper;
    }

    @Cacheable("certification-list")
    public PageResponse<CertificationResponse> search(SearchCertificationsQuery query) {
        log.debug("Searching certifications with criteria: {}", query);

        Sort sort = createSort(query.sortBy(), query.sortDirection());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        Page<CertificationSummaryProjection> page = certificationRepository.searchSummary(
                query.keyword(),
                query.provider(),
                query.level(),
                query.status(),
                query.difficultyLevel(),
                pageable
        );

        Page<CertificationResponse> responsePage = page.map(this::toCertificationResponse);
        return PageResponse.of(responsePage);
    }

    @Cacheable(value = "certification-detail", key = "#query.slug()")
    public CertificationDetailResponse getBySlug(GetCertificationBySlugQuery query) {
        log.debug("Getting certification by slug: {}", query.slug());

        Certification certification = certificationRepository.findActiveBySlug(query.slug())
                .orElseThrow(() -> ResourceNotFoundException.byField(CERTIFICATION, "slug", query.slug()));

        return certificationMapper.toDetailResponse(certification);
    }

    public CertificationDetailResponse getById(GetCertificationByIdQuery query) {
        log.debug("Getting certification by id: {}", query.id());

        Certification certification = certificationRepository.findActiveById(query.id())
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, query.id()));

        return certificationMapper.toDetailResponse(certification);
    }

    @Cacheable(value = "domain-tree", key = "#query.slug()")
    public List<CertificationDomainResponse> getDomainTree(GetCertificationDomainTreeBySlugQuery query) {
        log.debug("Getting domain tree for certification slug: {}", query.slug());

        Certification certification = certificationRepository.findActiveBySlug(query.slug())
                .orElseThrow(() -> ResourceNotFoundException.byField(CERTIFICATION, "slug", query.slug()));

        List<CertificationDomain> rootDomains = certificationDomainRepository
                .findByCertificationIdAndParentDomainIsNullOrderByDisplayOrderAsc(certification.getId());

        return certificationDomainMapper.buildDomainTree(rootDomains);
    }

    public Page<CertificationResponse> searchFullText(SearchCertificationsFullTextQuery query) {
        log.debug("Full-text searching certifications with query: {}", query.query());

        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<CertificationSummaryProjection> results = certificationRepository.searchFullTextSummary(query.query(), pageable);

        return results.map(this::toCertificationResponse);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }

    private CertificationResponse toCertificationResponse(CertificationSummaryProjection projection) {
        return CertificationResponse.builder()
                .id(projection.getId())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .createdBy(projection.getCreatedBy())
                .updatedBy(projection.getUpdatedBy())
                .name(projection.getName())
                .code(projection.getCode())
                .slug(projection.getSlug())
                .tagline(projection.getTagline())
                .provider(projection.getProvider())
                .description(projection.getDescription())
                .overview(projection.getOverview())
                .level(projection.getLevel())
                .difficultyLevel(projection.getDifficultyLevel())
                .durationMinutes(projection.getDurationMinutes())
                .passingScore(projection.getPassingScore())
                .totalQuestions(projection.getTotalQuestions())
                .price(projection.getPrice())
                .examPriceUsd(projection.getExamPriceUsd())
                .averagePrepWeeks(projection.getAveragePrepWeeks())
                .status(projection.getStatus())
                .build();
    }
}
