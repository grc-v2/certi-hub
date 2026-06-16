package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.adapter.web.request.CollectionRequest;
import com.certimaster.productivity.adapter.web.response.CollectionResponse;
import com.certimaster.productivity.application.command.CollectionCommandService;
import com.certimaster.productivity.application.command.CreateCollectionCommand;
import com.certimaster.productivity.application.command.DeleteCollectionCommand;
import com.certimaster.productivity.application.command.RenameCollectionCommand;
import com.certimaster.productivity.application.query.CollectionQueryService;
import com.certimaster.productivity.application.query.ListCollectionsQuery;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tracker/collections")
public class CollectionController {

    private final CollectionCommandService collectionCommandService;
    private final CollectionQueryService collectionQueryService;

    public CollectionController(CollectionCommandService collectionCommandService,
                                CollectionQueryService collectionQueryService) {
        this.collectionCommandService = collectionCommandService;
        this.collectionQueryService = collectionQueryService;
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponse>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(collectionQueryService.list(new ListCollectionsQuery(userId)));
    }

    @PostMapping
    public ResponseEntity<CollectionResponse> create(@RequestBody CollectionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CollectionResponse created = collectionCommandService.create(new CreateCollectionCommand(userId, request.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CollectionResponse> rename(@PathVariable Long id, @RequestBody CollectionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(collectionCommandService.rename(new RenameCollectionCommand(id, userId, request.getName())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        collectionCommandService.delete(new DeleteCollectionCommand(id, userId));
        return ResponseEntity.noContent().build();
    }
}
