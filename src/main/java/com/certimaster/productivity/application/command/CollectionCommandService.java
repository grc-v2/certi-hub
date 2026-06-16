package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.TrackerCollectionRepository;
import com.certimaster.productivity.adapter.persistence.TrackerResourceRepository;
import com.certimaster.productivity.adapter.web.mapper.CollectionMapper;
import com.certimaster.productivity.adapter.web.response.CollectionResponse;
import com.certimaster.productivity.domain.entity.TrackerCollection;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CollectionCommandService {

    private final TrackerCollectionRepository collectionRepository;
    private final TrackerResourceRepository resourceRepository;
    private final CollectionMapper collectionMapper;

    public CollectionCommandService(TrackerCollectionRepository collectionRepository,
                                    TrackerResourceRepository resourceRepository,
                                    CollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.resourceRepository = resourceRepository;
        this.collectionMapper = collectionMapper;
    }

    public CollectionResponse create(CreateCollectionCommand command) {
        String name = command.name() != null ? command.name().trim() : "";
        TrackerCollection collection = new TrackerCollection(command.userId(), name);
        return collectionMapper.toResponse(collectionRepository.save(collection));
    }

    public CollectionResponse rename(RenameCollectionCommand command) {
        TrackerCollection collection = collectionRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Collection", command.id()));
        collection.setName(command.name() != null ? command.name().trim() : collection.getName());
        return collectionMapper.toResponse(collection);
    }

    public void delete(DeleteCollectionCommand command) {
        TrackerCollection collection = collectionRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Collection", command.id()));
        // Detach resources first so they survive as "no collection".
        resourceRepository.clearCollection(command.userId(), collection.getId());
        collectionRepository.delete(collection);
    }
}
