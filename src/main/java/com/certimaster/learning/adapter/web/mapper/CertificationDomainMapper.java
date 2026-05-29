package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.application.command.CreateCertificationDomainCommand;
import com.certimaster.learning.application.command.UpdateCertificationDomainCommand;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.domain.entity.CertificationDomain;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CertificationDomainMapper {

    public CertificationDomainResponse toResponse(CertificationDomain domain) {
        if (domain == null) {
            return null;
        }

        return CertificationDomainResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .description(domain.getDescription())
                .weightPercentage(domain.getWeightPercentage())
                .displayOrder(domain.getDisplayOrder())
                .certificationId(domain.getCertification() != null ? domain.getCertification().getId() : null)
                .parentDomainId(domain.getParentDomain() != null ? domain.getParentDomain().getId() : null)
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    public CertificationDomain toEntity(CreateCertificationDomainCommand command) {
        CertificationDomain domain = new CertificationDomain();
        domain.setName(command.name());
        domain.setDescription(command.description());
        domain.setDisplayOrder(command.displayOrder());
        return domain;
    }

    public void updateEntity(CertificationDomain domain, UpdateCertificationDomainCommand command) {
        domain.setName(command.name());
        domain.setDescription(command.description());
        domain.setDisplayOrder(command.displayOrder());
    }

    public List<CertificationDomainResponse> buildDomainTree(List<CertificationDomain> rootDomains) {
        if (rootDomains == null || rootDomains.isEmpty()) {
            return Collections.emptyList();
        }

        return rootDomains.stream()
                .map(this::buildTreeNode)
                .toList();
    }

    private CertificationDomainResponse buildTreeNode(CertificationDomain domain) {
        CertificationDomainResponse response = toResponse(domain);

        if (domain.getChildDomains() != null && !domain.getChildDomains().isEmpty()) {
            List<CertificationDomainResponse> children = domain.getChildDomains().stream()
                    .map(this::buildTreeNode)
                    .toList();
            response.setChildren(children);
        } else {
            response.setChildren(Collections.emptyList());
        }

        return response;
    }
}
