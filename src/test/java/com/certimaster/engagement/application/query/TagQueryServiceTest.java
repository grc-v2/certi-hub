package com.certimaster.engagement.application.query;

import com.certimaster.engagement.adapter.web.mapper.PostMapper;
import com.certimaster.engagement.adapter.persistence.PostReadRepository;
import com.certimaster.engagement.application.query.SearchTagsQuery;
import com.certimaster.engagement.domain.entity.PostTag;
import com.certimaster.engagement.adapter.web.mapper.TagMapper;
import com.certimaster.engagement.adapter.persistence.PostTagReadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagQueryServiceTest {

    @Mock
    private PostTagReadRepository tagRepository;

    @Mock
    private PostReadRepository postRepository;

    @Mock
    private PostMapper postMapper;

    private TagQueryService service;

    @BeforeEach
    void setUp() {
        service = new TagQueryService(tagRepository, postRepository, new TagMapper(), postMapper);
    }

    @Test
    void searchReturnsPagedTagsWithPostCounts() {
        PostTag tag = PostTag.builder().name("Java").slug("java").build();
        tag.setId(3L);

        when(tagRepository.search(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(tag)));
        when(tagRepository.countPublishedPostsByTagId(3L)).thenReturn(7L);

        var result = service.search(new SearchTagsQuery("ja", 0, 10, "name", "ASC"));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Java");
        assertThat(result.getContent().getFirst().getPostCount()).isEqualTo(7L);
    }
}
