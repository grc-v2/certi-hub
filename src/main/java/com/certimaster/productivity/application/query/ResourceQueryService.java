package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.TrackerResourceRepository;
import com.certimaster.productivity.adapter.web.mapper.ResourceMapper;
import com.certimaster.productivity.adapter.web.response.ResourceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ResourceQueryService {

    private final TrackerResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public ResourceQueryService(TrackerResourceRepository resourceRepository,
                                ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    public List<ResourceResponse> list(ListResourcesQuery query) {
        return resourceRepository.findByUserIdOrderByCreatedAtDesc(query.userId())
                .stream()
                .filter(r -> query.kind() == null || r.getKind() == query.kind())
                .filter(r -> query.collectionId() == null || query.collectionId().equals(r.getCollectionId()))
                .filter(r -> query.certificationId() == null || query.certificationId().equals(r.getCertificationId()))
                .map(resourceMapper::toResponse)
                .toList();
    }
}
