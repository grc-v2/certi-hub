package com.certimaster.productivity.adapter.web.mapper;

import com.certimaster.productivity.adapter.web.response.ResourceResponse;
import com.certimaster.productivity.domain.entity.TrackerResource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public ResourceResponse toResponse(TrackerResource r) {
        if (r == null) {
            return null;
        }
        return ResourceResponse.builder()
                .id(r.getId())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .userId(r.getUserId())
                .kind(r.getKind() != null ? r.getKind().name() : null)
                .url(r.getUrl())
                .title(r.getTitle())
                .content(r.getContent())
                .collectionId(r.getCollectionId())
                .certificationId(r.getCertificationId())
                .build();
    }
}
