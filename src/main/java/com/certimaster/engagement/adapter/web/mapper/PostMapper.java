package com.certimaster.engagement.adapter.web.mapper;

import com.certimaster.engagement.domain.entity.ContentType;
import com.certimaster.engagement.domain.entity.DifficultyLevel;
import com.certimaster.engagement.application.command.CreatePostCommand;
import com.certimaster.engagement.application.command.UpdatePostCommand;
import com.certimaster.engagement.adapter.web.response.PostDetailResponse;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostTagMapping;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Post entities and DTOs.
 */
@Component
public class PostMapper {

    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }
        return PostResponse.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .createdBy(post.getCreatedBy())
                .updatedBy(post.getUpdatedBy())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .featuredImage(post.getFeaturedImage())
                .authorId(post.getAuthorId())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .publishedAt(post.getPublishedAt())
                .viewsCount(post.getViewsCount())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .readingTimeMinutes(post.getReadingTimeMinutes())
                .tags(mapTags(post.getTagMappings()))
                .contentType(post.getContentType() != null ? post.getContentType().name() : null)
                .difficultyLevel(post.getDifficultyLevel() != null ? post.getDifficultyLevel().name() : null)
                .subtitle(post.getSubtitle())
                .coverImageUrl(post.getCoverImageUrl())
                .pinned(post.getPinned())
                .featured(post.getFeatured())
                .build();
    }

    public List<PostResponse> toResponseList(List<Post> posts) {
        if (posts == null) {
            return Collections.emptyList();
        }
        return posts.stream()
                .filter(Objects::nonNull)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PostDetailResponse toDetailResponse(Post post) {
        if (post == null) {
            return null;
        }
        PostDetailResponse response = PostDetailResponse.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .createdBy(post.getCreatedBy())
                .updatedBy(post.getUpdatedBy())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .contentHtml(post.getContentHtml())
                .excerpt(post.getExcerpt())
                .featuredImage(post.getFeaturedImage())
                .authorId(post.getAuthorId())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .publishedAt(post.getPublishedAt())
                .viewsCount(post.getViewsCount())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .readingTimeMinutes(post.getReadingTimeMinutes())
                .seoTitle(post.getSeoTitle())
                .seoDescription(post.getSeoDescription())
                .seoKeywords(post.getSeoKeywords())
                .tags(mapTags(post.getTagMappings()))
                .build();

        response.setContentType(post.getContentType() != null ? post.getContentType().name() : null);
        response.setDifficultyLevel(post.getDifficultyLevel() != null ? post.getDifficultyLevel().name() : null);
        response.setSubtitle(post.getSubtitle());
        response.setCoverImageUrl(post.getCoverImageUrl());
        response.setCoverImageAlt(post.getCoverImageAlt());
        response.setOgImageUrl(null);
        response.setTableOfContents(post.getTableOfContents());
        response.setWordCount(post.getWordCount());
        response.setBookmarkCount(post.getBookmarkCount());
        response.setPinned(post.getPinned());
        response.setFeatured(post.getFeatured());
        response.setCategory(post.getCategory());
        response.setSeriesName(post.getSeriesName());
        response.setSeriesOrder(post.getSeriesOrder());

        return response;
    }

    public Post toEntity(CreatePostCommand command) {
        if (command == null) {
            return null;
        }
        return Post.builder()
                .title(command.title())
                .content(command.content())
                .excerpt(command.excerpt())
                .featuredImage(command.featuredImage())
                .seoTitle(command.seoTitle())
                .seoDescription(command.seoDescription())
                .seoKeywords(command.seoKeywords())
                .contentType(command.contentType() != null ? ContentType.valueOf(command.contentType()) : null)
                .difficultyLevel(command.difficultyLevel() != null ? DifficultyLevel.valueOf(command.difficultyLevel()) : null)
                .subtitle(command.subtitle())
                .coverImageUrl(command.coverImageUrl())
                .coverImageAlt(command.coverImageAlt())
                .viewsCount(0)
                .likesCount(0)
                .commentsCount(0)
                .build();
    }

    public void updateEntity(Post post, UpdatePostCommand command) {
        if (post == null || command == null) {
            return;
        }
        if (command.title() != null) {
            post.setTitle(command.title());
        }
        if (command.content() != null) {
            post.setContent(command.content());
        }
        if (command.excerpt() != null) {
            post.setExcerpt(command.excerpt());
        }
        if (command.featuredImage() != null) {
            post.setFeaturedImage(command.featuredImage());
        }
        if (command.seoTitle() != null) {
            post.setSeoTitle(command.seoTitle());
        }
        if (command.seoDescription() != null) {
            post.setSeoDescription(command.seoDescription());
        }
        if (command.seoKeywords() != null) {
            post.setSeoKeywords(command.seoKeywords());
        }
        if (command.contentType() != null) {
            post.setContentType(ContentType.valueOf(command.contentType()));
        }
        if (command.difficultyLevel() != null) {
            post.setDifficultyLevel(DifficultyLevel.valueOf(command.difficultyLevel()));
        }
        if (command.subtitle() != null) {
            post.setSubtitle(command.subtitle());
        }
        if (command.coverImageUrl() != null) {
            post.setCoverImageUrl(command.coverImageUrl());
        }
        if (command.coverImageAlt() != null) {
            post.setCoverImageAlt(command.coverImageAlt());
        }
    }

    private List<TagResponse> mapTags(Set<PostTagMapping> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyList();
        }
        return mappings.stream()
                .filter(Objects::nonNull)
                .filter(mapping -> mapping.getTag() != null)
                .map(mapping -> TagResponse.builder()
                        .id(mapping.getTag().getId())
                        .name(mapping.getTag().getName())
                        .slug(mapping.getTag().getSlug())
                        .build())
                .collect(Collectors.toList());
    }
}
