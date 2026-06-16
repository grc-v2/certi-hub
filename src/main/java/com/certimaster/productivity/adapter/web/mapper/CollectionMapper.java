package com.certimaster.productivity.adapter.web.mapper;

import com.certimaster.productivity.adapter.web.response.CollectionResponse;
import com.certimaster.productivity.domain.entity.TrackerCollection;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionResponse toResponse(TrackerCollection c) {
        if (c == null) {
            return null;
        }
        return new CollectionResponse(c.getId(), c.getName());
    }
}
