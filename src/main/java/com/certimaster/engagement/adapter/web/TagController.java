package com.certimaster.engagement.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.engagement.application.command.CreateTagCommand;
import com.certimaster.engagement.application.command.DeleteTagCommand;
import com.certimaster.engagement.application.command.UpdateTagCommand;
import com.certimaster.engagement.application.query.GetPostsByTagQuery;
import com.certimaster.engagement.application.query.GetTagBySlugQuery;
import com.certimaster.engagement.application.query.SearchTagsQuery;
import com.certimaster.engagement.adapter.web.request.TagRequest;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import com.certimaster.engagement.application.command.TagCommandService;
import com.certimaster.engagement.application.query.TagQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {


    private final TagCommandService tagCommandService;
    private final TagQueryService tagQueryService;

    public TagController(TagCommandService tagCommandService, TagQueryService tagQueryService) {
        this.tagCommandService = tagCommandService;
        this.tagQueryService = tagQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<TagResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        SearchTagsQuery query = new SearchTagsQuery(keyword, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(tagQueryService.search(query));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<TagResponse> getTagBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(tagQueryService.getTagBySlug(new GetTagBySlugQuery(slug)));
    }

    @GetMapping("/{slug}/posts")
    public ResponseEntity<PageResponse<PostResponse>> getPostsByTag(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(tagQueryService.getPostsByTag(new GetPostsByTagQuery(slug, page, size)));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagCommandService.createTag(new CreateTagCommand(request.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request
    ) {
        return ResponseEntity.ok(tagCommandService.updateTag(new UpdateTagCommand(id, request.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagCommandService.deleteTag(new DeleteTagCommand(id));
        return ResponseEntity.noContent().build();
    }
}
