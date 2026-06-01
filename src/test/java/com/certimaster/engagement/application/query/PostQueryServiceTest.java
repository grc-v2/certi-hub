package com.certimaster.engagement.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.engagement.adapter.persistence.PostReadRepository;
import com.certimaster.engagement.adapter.persistence.projection.PostSummaryProjection;
import com.certimaster.engagement.adapter.persistence.projection.PostTagSummaryProjection;
import com.certimaster.engagement.adapter.web.mapper.PostMapper;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.domain.service.MarkdownService;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {

    @Mock
    private PostReadRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private MarkdownService markdownService;

    @InjectMocks
    private PostQueryService service;

    @Test
    void searchPostsReturnsPagedResponses() {
        SearchPostsQuery query = new SearchPostsQuery(
                "java",
                "PUBLISHED",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                10,
                "createdAt",
                "DESC"
        );
        Post post = new Post();
        PostResponse response = PostResponse.builder().id(1L).title("Java CQRS").build();

        when(postRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Post>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post)));
        when(postMapper.toResponse(post)).thenReturn(response);

        PageResponse<PostResponse> result = service.searchPosts(query);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void trendingPostsUseProjectionAndBatchTagLookup() {
        PostSummaryProjection post = mock(PostSummaryProjection.class);
        when(post.getId()).thenReturn(1L);
        when(post.getTitle()).thenReturn("Java CQRS");
        when(post.getSlug()).thenReturn("java-cqrs");
        when(post.getStatus()).thenReturn("PUBLISHED");
        when(post.getViewsCount()).thenReturn(100);
        when(post.getLikesCount()).thenReturn(10);
        when(post.getCommentsCount()).thenReturn(5);

        PostTagSummaryProjection tag = mock(PostTagSummaryProjection.class);
        when(tag.getPostId()).thenReturn(1L);
        when(tag.getTagId()).thenReturn(9L);
        when(tag.getName()).thenReturn("Java");
        when(tag.getSlug()).thenReturn("java");

        when(postRepository.findTrendingPostSummaries(eq(PostStatus.PUBLISHED.name()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post)));
        when(postRepository.findTagSummariesByPostIds(List.of(1L))).thenReturn(List.of(tag));

        PageResponse<PostResponse> result = service.getTrendingPosts(new GetTrendingPostsQuery(0, 10));

        PostResponse response = result.getContent().getFirst();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Java CQRS");
        assertThat(response.getStatus()).isEqualTo("PUBLISHED");
        assertThat(response.getViewsCount()).isEqualTo(100);
        assertThat(response.getTags()).extracting(TagResponse::getName).containsExactly("Java");
        verify(postRepository).findTagSummariesByPostIds(List.of(1L));
    }

    @Test
    void authorPostsUseUnsortedPageableBecauseNativeQueryOwnsOrdering() {
        PostSummaryProjection post = mock(PostSummaryProjection.class);
        when(post.getId()).thenReturn(1L);

        when(postRepository.findByAuthorIdSummary(eq(42L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post)));
        when(postRepository.findTagSummariesByPostIds(List.of(1L))).thenReturn(List.of());

        service.getPostsByAuthor(new GetPostsByAuthorQuery(42L, 0, 10));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(postRepository).findByAuthorIdSummary(eq(42L), pageable.capture());
        assertThat(pageable.getValue().getSort().isUnsorted()).isTrue();
    }
}
