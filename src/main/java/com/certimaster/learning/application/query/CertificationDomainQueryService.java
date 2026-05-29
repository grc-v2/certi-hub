package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.application.query.GetCertificationDomainByIdQuery;
import com.certimaster.learning.application.query.GetCertificationDomainTreeQuery;
import com.certimaster.learning.application.query.GetCertificationDomainsQuery;
import com.certimaster.learning.application.query.SearchCertificationDomainsQuery;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.adapter.web.mapper.CertificationDomainMapper;
import com.certimaster.learning.adapter.persistence.CertificationDomainReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CertificationDomainQueryService {

    private static final String DOMAIN = "CertificationDomain";

    private final CertificationDomainReadRepository certificationDomainRepository;
    private final CertificationDomainMapper certificationDomainMapper;

    public CertificationDomainQueryService(CertificationDomainReadRepository certificationDomainRepository,
                                           CertificationDomainMapper certificationDomainMapper) {
        this.certificationDomainRepository = certificationDomainRepository;
        this.certificationDomainMapper = certificationDomainMapper;
    }

    public CertificationDomainResponse getById(GetCertificationDomainByIdQuery query) {
        CertificationDomain domain = findOrThrow(query.id());
        return certificationDomainMapper.toResponse(domain);
    }

    public PageResponse<CertificationDomainResponse> search(SearchCertificationDomainsQuery query) {
        Sort sort = "desc".equalsIgnoreCase(query.sortDir())
                ? Sort.by(query.sortBy()).descending()
                : Sort.by(query.sortBy()).ascending();

        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);
        Page<CertificationDomain> page = certificationDomainRepository.search(query.keyword(), pageable);

        return PageResponse.of(page.map(certificationDomainMapper::toResponse));
    }

    public List<CertificationDomainResponse> getByCertificationId(GetCertificationDomainsQuery query) {
        return certificationDomainRepository.findByCertificationIdOrderByDisplayOrderAsc(query.certificationId())
                .stream()
                .map(certificationDomainMapper::toResponse)
                .toList();
    }

    public List<CertificationDomainResponse> getDomainTree(GetCertificationDomainTreeQuery query) {
        List<CertificationDomain> rootDomains = certificationDomainRepository
                .findByCertificationIdAndParentDomainIsNullOrderByDisplayOrderAsc(query.certificationId());
        return certificationDomainMapper.buildDomainTree(rootDomains);
    }

    private CertificationDomain findOrThrow(Long id) {
        return certificationDomainRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(DOMAIN, id));
    }
}
