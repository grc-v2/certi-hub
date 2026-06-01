package com.certimaster.engagement.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.engagement.adapter.persistence.projection.PostSummaryProjection;
import com.certimaster.engagement.adapter.persistence.projection.PostTagSummaryProjection;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.engagement.adapter.persistence.PostReadRepository;
import com.certimaster.engagement.adapter.persistence.PostSpecification;
import com.certimaster.engagement.adapter.web.mapper.PostMapper;
import com.certimaster.engagement.adapter.web.response.PostDetailResponse;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.domain.service.MarkdownService;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostQueryService {


    private final PostReadRepository postRepository;
    private final PostMapper postMapper;
    private final MarkdownService markdownService;

    public PostQueryService(PostReadRepository postRepository,
                            PostMapper postMapper,
                            MarkdownService markdownService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.markdownService = markdownService;
    }

    public PostDetailResponse getPublishedPost(GetPublishedPostQuery query) {
        Post post = postRepository.findBySlug(query.slug())
                .orElseThrow(() -> ResourceNotFoundException.byField("Post", "slug", query.slug()));
        return buildDetailResponse(post);
    }

    public PostDetailResponse getPostForEdit(GetPostForEditQuery query) {
        Post post = postRepository.findById(query.id())
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", query.id()));
        return buildDetailResponse(post);
    }

    public PageResponse<PostResponse> searchPosts(SearchPostsQuery query) {
        Pageable pageable = buildPageable(query);
        Specification<Post> spec = PostSpecification.fromSearchQuery(query);
        Page<Post> page = postRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(postMapper::toResponse));
    }

    public PageResponse<PostResponse> getPostsByAuthor(GetPostsByAuthorQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<PostSummaryProjection> postPage = postRepository.findByAuthorIdSummary(query.authorId(), pageable);
        return toPostSummaryPage(postPage);
    }

    public PageResponse<PostResponse> getPostsByDomain(GetPostsByDomainQuery query) {
        // Certification mapping removed - return empty
        return PageResponse.of(Page.empty());
    }

    public PageResponse<PostResponse> getPostsByTags(GetPostsByTagsQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<PostSummaryProjection> postPage = postRepository.findByTagNamesAndStatusSummary(
                query.tagNames(),
                PostStatus.PUBLISHED.name(),
                pageable);
        return toPostSummaryPage(postPage);
    }

    public PageResponse<PostResponse> getTrendingPosts(GetTrendingPostsQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<PostSummaryProjection> postPage = postRepository.findTrendingPostSummaries(
                PostStatus.PUBLISHED.name(),
                pageable);
        return toPostSummaryPage(postPage);
    }

    public PageResponse<PostResponse> getFeaturedPosts(GetFeaturedPostsQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<PostSummaryProjection> postPage = postRepository.findFeaturedPostSummaries(
                PostStatus.PUBLISHED.name(),
                pageable);
        return toPostSummaryPage(postPage);
    }

    public PageResponse<PostResponse> getAllPosts(SearchPostsQuery query) {
        Pageable pageable = buildPageable(query);
        Specification<Post> spec = PostSpecification.fromSearchQuery(query);
        Page<Post> page = postRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(postMapper::toResponse));
    }

    private PostDetailResponse buildDetailResponse(Post post) {
        PostDetailResponse response = postMapper.toDetailResponse(post);

        if (response.getContentHtml() == null || response.getContentHtml().isEmpty()) {
            response.setContentHtml(markdownService.renderToHtml(post.getContent()));
        }

        return response;
    }

    private PageResponse<PostResponse> toPostSummaryPage(Page<PostSummaryProjection> page) {
        Map<Long, List<TagResponse>> tagsByPostId = loadTagsByPostId(page.getContent().stream()
                .map(PostSummaryProjection::getId)
                .toList());
        return PageResponse.of(page.map(post -> toPostResponse(post, tagsByPostId.getOrDefault(post.getId(), List.of()))));
    }

    private Map<Long, List<TagResponse>> loadTagsByPostId(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }
        return postRepository.findTagSummariesByPostIds(postIds).stream()
                .collect(Collectors.groupingBy(
                        PostTagSummaryProjection::getPostId,
                        Collectors.mapping(this::toTagResponse, Collectors.toList())
                ));
    }

    private TagResponse toTagResponse(PostTagSummaryProjection projection) {
        return TagResponse.builder()
                .id(projection.getTagId())
                .name(projection.getName())
                .slug(projection.getSlug())
                .build();
    }

    private PostResponse toPostResponse(PostSummaryProjection projection, List<TagResponse> tags) {
        return PostResponse.builder()
                .id(projection.getId())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .createdBy(projection.getCreatedBy())
                .updatedBy(projection.getUpdatedBy())
                .title(projection.getTitle())
                .slug(projection.getSlug())
                .excerpt(projection.getExcerpt())
                .featuredImage(projection.getFeaturedImage())
                .authorId(projection.getAuthorId())
                .status(projection.getStatus())
                .publishedAt(projection.getPublishedAt())
                .viewsCount(projection.getViewsCount())
                .likesCount(projection.getLikesCount())
                .commentsCount(projection.getCommentsCount())
                .readingTimeMinutes(projection.getReadingTimeMinutes())
                .tags(tags)
                .contentType(projection.getContentType())
                .difficultyLevel(projection.getDifficultyLevel())
                .subtitle(projection.getSubtitle())
                .coverImageUrl(projection.getCoverImageUrl())
                .pinned(projection.getPinned())
                .featured(projection.getFeatured())
                .category(projection.getCategory())
                .build();
    }

    private Pageable buildPageable(SearchPostsQuery query) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(query.sortDirection())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = query.sortBy() != null ? query.sortBy() : "createdAt";
        return PageRequest.of(
                query.page() != null ? query.page() : 0,
                query.size() != null ? query.size() : 10,
                Sort.by(direction, sortBy)
        );
    }
}
