package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.external.WebPageTitleFetcher;
import com.certimaster.productivity.adapter.persistence.TrackerResourceRepository;
import com.certimaster.productivity.adapter.web.mapper.ResourceMapper;
import com.certimaster.productivity.adapter.web.response.ResourceResponse;
import com.certimaster.productivity.domain.entity.ResourceKind;
import com.certimaster.productivity.domain.entity.TrackerResource;
import com.certimaster.productivity.domain.service.ResourceFileStorageService;
import com.certimaster.productivity.domain.service.ResourceKindDetector;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ResourceCommandService {

    private static final int DERIVED_TITLE_MAX = 80;

    private static final int FILE_TITLE_MAX = 200;

    private final TrackerResourceRepository resourceRepository;
    private final WebPageTitleFetcher titleFetcher;
    private final ResourceMapper resourceMapper;
    private final ResourceFileStorageService fileStorage;

    public ResourceCommandService(TrackerResourceRepository resourceRepository,
                                  WebPageTitleFetcher titleFetcher,
                                  ResourceMapper resourceMapper,
                                  ResourceFileStorageService fileStorage) {
        this.resourceRepository = resourceRepository;
        this.titleFetcher = titleFetcher;
        this.resourceMapper = resourceMapper;
        this.fileStorage = fileStorage;
    }

    /** Upload a PDF/ebook and store it as a {@code PDF} resource (url = object key). */
    public ResourceResponse createPdf(Long userId, MultipartFile file, String title, Long collectionId) {
        String objectKey = fileStorage.uploadPdf(userId, file);
        String resolvedTitle = trimToNull(title);
        if (resolvedTitle == null) {
            resolvedTitle = deriveFileTitle(file.getOriginalFilename());
        }
        TrackerResource resource = TrackerResource.builder()
                .userId(userId)
                .kind(ResourceKind.PDF)
                .url(objectKey)
                .title(resolvedTitle)
                .collectionId(collectionId)
                .build();
        return resourceMapper.toResponse(resourceRepository.save(resource));
    }

    /** A short-lived presigned URL to open the owner's PDF file. */
    @Transactional(readOnly = true)
    public String presignFileUrl(Long resourceId, Long userId) {
        TrackerResource resource = resourceRepository.findByIdAndUserId(resourceId, userId)
                .orElseThrow(() -> ResourceNotFoundException.byId("Resource", resourceId));
        if (resource.getKind() != ResourceKind.PDF || resource.getUrl() == null) {
            throw new IllegalArgumentException("Resource has no downloadable file");
        }
        return fileStorage.presignedGetUrl(resource.getUrl());
    }

    public ResourceResponse create(CreateResourceCommand command) {
        String url = trimToNull(command.url());
        String title = trimToNull(command.title());
        ResourceKind kind;

        if (url != null) {
            kind = command.kindOverride() != null && command.kindOverride() != ResourceKind.NOTE
                    ? command.kindOverride()
                    : ResourceKindDetector.detect(url);
            if (title == null) {
                title = titleFetcher.fetchTitle(url);
            }
        } else {
            kind = ResourceKind.NOTE;
            if (title == null) {
                title = deriveNoteTitle(command.content());
            }
        }

        TrackerResource resource = TrackerResource.builder()
                .userId(command.userId())
                .kind(kind)
                .url(url)
                .title(title)
                .content(command.content())
                .collectionId(command.collectionId())
                .build();

        return resourceMapper.toResponse(resourceRepository.save(resource));
    }

    public ResourceResponse setCollection(SetResourceCollectionCommand command) {
        TrackerResource resource = resourceRepository.findByIdAndUserId(command.resourceId(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Resource", command.resourceId()));
        resource.setCollectionId(command.collectionId());
        return resourceMapper.toResponse(resource);
    }

    public ResourceResponse update(UpdateResourceCommand command) {
        TrackerResource resource = resourceRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Resource", command.id()));

        if (command.title() != null) {
            resource.setTitle(trimToNull(command.title()));
        }
        resource.setContent(command.content());

        // For a link resource, a changed URL re-detects the kind. NOTE and PDF
        // resources keep their kind (PDF's url is an internal object key, not a link).
        String newUrl = trimToNull(command.url());
        if (resource.getKind() != ResourceKind.NOTE && resource.getKind() != ResourceKind.PDF
                && newUrl != null && !newUrl.equals(resource.getUrl())) {
            resource.setUrl(newUrl);
            resource.setKind(ResourceKindDetector.detect(newUrl));
        }

        // A note with no title falls back to a derived one so it stays displayable.
        if (resource.getKind() == ResourceKind.NOTE && trimToNull(resource.getTitle()) == null) {
            resource.setTitle(deriveNoteTitle(resource.getContent()));
        }

        return resourceMapper.toResponse(resource);
    }

    public void delete(DeleteResourceCommand command) {
        TrackerResource resource = resourceRepository.findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Resource", command.id()));
        if (resource.getKind() == ResourceKind.PDF && resource.getUrl() != null) {
            fileStorage.delete(resource.getUrl());
        }
        resourceRepository.delete(resource);
    }

    private String deriveFileTitle(String filename) {
        String name = trimToNull(filename);
        if (name == null) {
            return "Tài liệu PDF";
        }
        if (name.toLowerCase().endsWith(".pdf")) {
            name = name.substring(0, name.length() - 4);
        }
        name = name.trim();
        if (name.isEmpty()) {
            return "Tài liệu PDF";
        }
        return name.length() > FILE_TITLE_MAX ? name.substring(0, FILE_TITLE_MAX) : name;
    }

    private String deriveNoteTitle(String content) {
        String text = trimToNull(content);
        if (text == null) {
            return "Ghi chú";
        }
        String firstLine = text.lines().findFirst().orElse(text).trim();
        if (firstLine.isEmpty()) {
            return "Ghi chú";
        }
        return firstLine.length() > DERIVED_TITLE_MAX ? firstLine.substring(0, DERIVED_TITLE_MAX) : firstLine;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
