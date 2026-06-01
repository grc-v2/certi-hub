package com.certimaster.engagement.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.engagement.application.command.ArchivePostCommand;
import com.certimaster.engagement.application.command.CreatePostCommand;
import com.certimaster.engagement.application.command.DeletePostCommand;
import com.certimaster.engagement.application.command.PublishPostCommand;
import com.certimaster.engagement.application.command.SchedulePostCommand;
import com.certimaster.engagement.application.command.SubmitPostForReviewCommand;
import com.certimaster.engagement.application.command.UpdatePostCommand;
import com.certimaster.engagement.application.query.GetFeaturedPostsQuery;
import com.certimaster.engagement.application.query.GetPostsByAuthorQuery;
import com.certimaster.engagement.application.query.GetPublishedPostQuery;
import com.certimaster.engagement.application.query.GetTrendingPostsQuery;
import com.certimaster.engagement.application.query.SearchPostsQuery;
import com.certimaster.engagement.adapter.web.request.PostRequest;
import com.certimaster.engagement.adapter.web.response.PostDetailResponse;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.application.command.PostCommandService;
import com.certimaster.engagement.application.query.PostQueryService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {


    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    public PostController(PostCommandService postCommandService,
                          PostQueryService postQueryService) {
        this.postCommandService = postCommandService;
        this.postQueryService = postQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishedFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishedTo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        SearchPostsQuery query = new SearchPostsQuery(
                keyword,
                status,
                authorId,
                null,
                tagId,
                contentType,
                difficultyLevel,
                null,
                null,
                publishedFrom,
                publishedTo,
                page,
                size,
                sortBy,
                sortDirection
        );

        return ResponseEntity.ok(postQueryService.searchPosts(query));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetailResponse> getPostBySlug(@PathVariable String slug) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(postQueryService.getPublishedPost(new GetPublishedPostQuery(slug, currentUserId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BLOG_CREATE')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        Long authorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(postCommandService.createPost(toCreateCommand(request, authorId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_UPDATE') or hasAuthority('BLOG_CREATE')")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id, @Valid @RequestBody PostRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(postCommandService.updatePost(toUpdateCommand(id, request, userId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BLOG_DELETE') or hasAuthority('BLOG_CREATE')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.deletePost(new DeletePostCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('BLOG_PUBLISH')")
    public ResponseEntity<PostResponse> publishPost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(postCommandService.publishPost(new PublishPostCommand(id, userId)));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('BLOG_PUBLISH')")
    public ResponseEntity<PostResponse> archivePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(postCommandService.archivePost(new ArchivePostCommand(id, userId)));
    }

    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasAuthority('BLOG_PUBLISH')")
    public ResponseEntity<PostResponse> schedulePost(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(postCommandService.schedulePost(new SchedulePostCommand(id, scheduledAt, userId)));
    }

    @PostMapping("/{id}/submit-review")
    @PreAuthorize("hasAuthority('BLOG_CREATE')")
    public ResponseEntity<PostResponse> submitForReview(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(postCommandService.submitForReview(new SubmitPostForReviewCommand(id, userId)));
    }

    @GetMapping("/trending")
    public ResponseEntity<PageResponse<PostResponse>> getTrendingPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(postQueryService.getTrendingPosts(new GetTrendingPostsQuery(page, size)));
    }

    @GetMapping("/featured")
    public ResponseEntity<PageResponse<PostResponse>> getFeaturedPosts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(postQueryService.getFeaturedPosts(new GetFeaturedPostsQuery(page, size)));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<PageResponse<PostResponse>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(postQueryService.getPostsByAuthor(new GetPostsByAuthorQuery(authorId, page, size)));
    }

    private CreatePostCommand toCreateCommand(PostRequest request, Long authorId) {
        return new CreatePostCommand(
                request.getTitle(),
                request.getContent(),
                request.getExcerpt(),
                request.getFeaturedImage(),
                request.getCategoryIds(),
                request.getTagNames(),
                request.getSeoTitle(),
                request.getSeoDescription(),
                request.getSeoKeywords(),
                request.getContentType(),
                request.getDifficultyLevel(),
                request.getSubtitle(),
                request.getCoverImageUrl(),
                request.getCoverImageAlt(),
                request.getOgImageUrl(),
                request.getSeriesId(),
                authorId
        );
    }

    private UpdatePostCommand toUpdateCommand(Long id, PostRequest request, Long userId) {
        return new UpdatePostCommand(
                id,
                request.getTitle(),
                request.getContent(),
                request.getExcerpt(),
                request.getFeaturedImage(),
                request.getCategoryIds(),
                request.getTagNames(),
                request.getSeoTitle(),
                request.getSeoDescription(),
                request.getSeoKeywords(),
                request.getContentType(),
                request.getDifficultyLevel(),
                request.getSubtitle(),
                request.getCoverImageUrl(),
                request.getCoverImageAlt(),
                request.getOgImageUrl(),
                request.getSeriesId(),
                userId
        );
    }
}
