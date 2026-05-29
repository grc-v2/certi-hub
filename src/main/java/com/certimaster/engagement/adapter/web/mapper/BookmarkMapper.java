package com.certimaster.engagement.adapter.web.mapper;

import com.certimaster.engagement.adapter.web.response.BookmarkResponse;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import com.certimaster.engagement.domain.entity.Bookmark;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostTagMapping;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookmarkMapper {

    public BookmarkResponse toResponse(Bookmark bookmark) {
        if (bookmark == null) {
            return null;
        }
        Post post = bookmark.getPost();
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .createdAt(bookmark.getCreatedAt())
                .updatedAt(bookmark.getUpdatedAt())
                .createdBy(bookmark.getCreatedBy())
                .updatedBy(bookmark.getUpdatedBy())
                .userId(bookmark.getUserId())
                .notes(bookmark.getNotes())
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
                .category(post.getCategory())
                .build();
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
