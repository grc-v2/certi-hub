package com.certimaster.productivity.adapter.web;

import com.certimaster.productivity.adapter.web.request.CreateResourceRequest;
import com.certimaster.productivity.adapter.web.request.SetResourceCertificationRequest;
import com.certimaster.productivity.adapter.web.request.SetResourceCollectionRequest;
import com.certimaster.productivity.adapter.web.request.UpdateResourceRequest;
import com.certimaster.productivity.adapter.web.response.ResourceResponse;
import com.certimaster.productivity.application.command.CreateResourceCommand;
import com.certimaster.productivity.application.command.DeleteResourceCommand;
import com.certimaster.productivity.application.command.ResourceCommandService;
import com.certimaster.productivity.application.command.SetResourceCertificationCommand;
import com.certimaster.productivity.application.command.SetResourceCollectionCommand;
import com.certimaster.productivity.application.command.UpdateResourceCommand;
import com.certimaster.productivity.application.query.ListResourcesQuery;
import com.certimaster.productivity.application.query.ResourceQueryService;
import com.certimaster.productivity.domain.entity.ResourceKind;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tracker/resources")
public class ResourceController {

    private final ResourceCommandService resourceCommandService;
    private final ResourceQueryService resourceQueryService;

    public ResourceController(ResourceCommandService resourceCommandService,
                              ResourceQueryService resourceQueryService) {
        this.resourceCommandService = resourceCommandService;
        this.resourceQueryService = resourceQueryService;
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> list(
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) Long collectionId,
            @RequestParam(required = false) Long certificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(resourceQueryService.list(
                new ListResourcesQuery(userId, parseKind(kind), collectionId, certificationId)));
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> create(@RequestBody CreateResourceRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ResourceResponse created = resourceCommandService.create(new CreateResourceCommand(
                userId,
                request.getUrl(),
                request.getTitle(),
                request.getContent(),
                parseKind(request.getKind()),
                request.getCollectionId(),
                request.getCertificationId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResourceResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long collectionId,
            @RequestParam(required = false) Long certificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ResourceResponse created = resourceCommandService.createPdf(userId, file, title, collectionId, certificationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Map<String, String>> fileUrl(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(Map.of("url", resourceCommandService.presignFileUrl(id, userId)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResourceResponse> update(@PathVariable Long id,
                                                   @RequestBody UpdateResourceRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        ResourceResponse updated = resourceCommandService.update(new UpdateResourceCommand(
                id,
                userId,
                request.getTitle(),
                request.getContent(),
                request.getUrl()));
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/collection")
    public ResponseEntity<ResourceResponse> setCollection(@PathVariable Long id,
                                                          @RequestBody SetResourceCollectionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(resourceCommandService.setCollection(
                new SetResourceCollectionCommand(id, userId, request.getCollectionId())));
    }

    @PatchMapping("/{id}/certification")
    public ResponseEntity<ResourceResponse> setCertification(@PathVariable Long id,
                                                             @RequestBody SetResourceCertificationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(resourceCommandService.setCertification(
                new SetResourceCertificationCommand(id, userId, request.getCertificationId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        resourceCommandService.delete(new DeleteResourceCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    private static ResourceKind parseKind(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return ResourceKind.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
