package com.certimaster.learning.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.learning.application.query.GetCertificationByIdQuery;
import com.certimaster.learning.application.query.GetCertificationBySlugQuery;
import com.certimaster.learning.application.query.GetCertificationDomainTreeBySlugQuery;
import com.certimaster.learning.application.query.SearchCertificationsFullTextQuery;
import com.certimaster.learning.application.query.SearchCertificationsQuery;
import com.certimaster.learning.adapter.web.response.CertificationDetailResponse;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.adapter.web.response.CertificationResponse;
import com.certimaster.learning.application.query.CertificationQueryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationController {

    private final CertificationQueryService certificationQueryService;

    public CertificationController(CertificationQueryService certificationQueryService) {
        this.certificationQueryService = certificationQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CertificationResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        SearchCertificationsQuery query = new SearchCertificationsQuery(
                keyword,
                provider,
                level,
                status,
                providerId,
                difficultyLevel,
                page,
                size,
                sortBy,
                sortDirection
        );

        return ResponseEntity.ok(certificationQueryService.search(query));
    }

    @GetMapping("/options")
    public ResponseEntity<List<com.certimaster.learning.adapter.web.response.CertificationOptionResponse>> getOptions(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return ResponseEntity.ok(certificationQueryService.getOptions(includeInactive));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CertificationDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationQueryService.getById(new GetCertificationByIdQuery(id)));
    }

    @GetMapping("/{slug:[a-zA-Z][-\\w]*}")
    public ResponseEntity<CertificationDetailResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(certificationQueryService.getBySlug(new GetCertificationBySlugQuery(slug)));
    }

    @GetMapping("/{slug}/domains")
    public ResponseEntity<List<CertificationDomainResponse>> getDomainTree(@PathVariable String slug) {
        return ResponseEntity.ok(certificationQueryService.getDomainTree(new GetCertificationDomainTreeBySlugQuery(slug)));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<CertificationResponse>> searchFullText(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CertificationResponse> results = certificationQueryService.searchFullText(new SearchCertificationsFullTextQuery(query, page, size));
        return ResponseEntity.ok(PageResponse.of(results));
    }
}
