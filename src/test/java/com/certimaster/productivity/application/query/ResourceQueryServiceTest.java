package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.TrackerResourceRepository;
import com.certimaster.productivity.adapter.web.mapper.ResourceMapper;
import com.certimaster.productivity.adapter.web.response.ResourceResponse;
import com.certimaster.productivity.domain.entity.ResourceKind;
import com.certimaster.productivity.domain.entity.TrackerResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceQueryServiceTest {

    @Mock
    private TrackerResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceQueryService service;

    @Test
    void listFiltersByCertificationId() {
        TrackerResource forCert = resource(ResourceKind.NOTE, null, 42L);
        TrackerResource otherCert = resource(ResourceKind.NOTE, null, 7L);
        TrackerResource noCert = resource(ResourceKind.GITHUB, null, null);
        when(resourceRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(forCert, otherCert, noCert));
        when(resourceMapper.toResponse(any())).thenAnswer(inv -> {
            ResourceResponse r = new ResourceResponse();
            r.setCertificationId(((TrackerResource) inv.getArgument(0)).getCertificationId());
            return r;
        });

        List<ResourceResponse> result = service.list(new ListResourcesQuery(1L, null, null, 42L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCertificationId()).isEqualTo(42L);
    }

    @Test
    void listWithoutCertificationFilterReturnsEverything() {
        when(resourceRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(
                        resource(ResourceKind.NOTE, null, 42L),
                        resource(ResourceKind.GITHUB, null, null)));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());

        assertThat(service.list(new ListResourcesQuery(1L, null, null, null))).hasSize(2);
    }

    @Test
    void listCombinesKindAndCertificationFilters() {
        when(resourceRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(
                        resource(ResourceKind.YOUTUBE, null, 42L),
                        resource(ResourceKind.NOTE, null, 42L),
                        resource(ResourceKind.YOUTUBE, null, null)));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());

        assertThat(service.list(new ListResourcesQuery(1L, ResourceKind.YOUTUBE, null, 42L))).hasSize(1);
    }

    private TrackerResource resource(ResourceKind kind, Long collectionId, Long certificationId) {
        return TrackerResource.builder()
                .userId(1L)
                .kind(kind)
                .collectionId(collectionId)
                .certificationId(certificationId)
                .build();
    }
}
