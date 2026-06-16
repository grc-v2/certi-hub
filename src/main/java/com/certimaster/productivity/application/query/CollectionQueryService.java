package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.TrackerCollectionRepository;
import com.certimaster.productivity.adapter.web.mapper.CollectionMapper;
import com.certimaster.productivity.adapter.web.response.CollectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CollectionQueryService {

    private final TrackerCollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;

    public CollectionQueryService(TrackerCollectionRepository collectionRepository,
                                  CollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.collectionMapper = collectionMapper;
    }

    public List<CollectionResponse> list(ListCollectionsQuery query) {
        return collectionRepository.findByUserIdOrderByNameAsc(query.userId())
                .stream()
                .map(collectionMapper::toResponse)
                .toList();
    }
}
