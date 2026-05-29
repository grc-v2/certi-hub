package com.certimaster.engagement.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.engagement.application.command.FeaturePostCommand;
import com.certimaster.engagement.application.command.PinPostCommand;
import com.certimaster.engagement.application.query.SearchPostsQuery;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.application.command.PostCommandService;
import com.certimaster.engagement.application.query.PostQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    public AdminPostController(PostQueryService postQueryService, PostCommandService postCommandService) {
        this.postQueryService = postQueryService;
        this.postCommandService = postCommandService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> getAllPosts(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        SearchPostsQuery query = new SearchPostsQuery(
                null,
                status,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                page,
                size,
                sortBy,
                sortDirection
        );
        return ResponseEntity.ok(postQueryService.getAllPosts(query));
    }

    @PostMapping("/{id}/feature")
    public ResponseEntity<PostResponse> featurePost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean featured) {
        return ResponseEntity.ok(postCommandService.featurePost(new FeaturePostCommand(id, featured)));
    }

    @PostMapping("/{id}/pin")
    public ResponseEntity<PostResponse> pinPost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean pinned) {
        return ResponseEntity.ok(postCommandService.pinPost(new PinPostCommand(id, pinned)));
    }
}
