package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.persistence.PostTagMappingRepository;
import com.certimaster.engagement.application.command.CreateTagCommand;
import com.certimaster.engagement.application.command.DeleteTagCommand;
import com.certimaster.engagement.domain.entity.PostTag;
import com.certimaster.engagement.adapter.web.mapper.TagMapper;
import com.certimaster.engagement.adapter.persistence.PostTagWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagCommandServiceTest {

    @Mock
    private PostTagWriteRepository tagRepository;

    @Mock
    private PostTagMappingRepository tagMappingRepository;

    @Mock
    private PostWriteRepository postRepository;

    private TagCommandService service;

    @BeforeEach
    void setUp() {
        service = new TagCommandService(tagRepository, tagMappingRepository, postRepository, new TagMapper());
    }

    @Test
    void createTagGeneratesUniqueSlug() {
        when(tagRepository.existsByName("Spring Security")).thenReturn(false);
        when(tagRepository.existsBySlug("spring-security")).thenReturn(false);
        when(tagRepository.save(any(PostTag.class))).thenAnswer(invocation -> {
            PostTag tag = invocation.getArgument(0);
            tag.setId(5L);
            return tag;
        });
        when(tagRepository.countPublishedPostsByTagId(5L)).thenReturn(0L);

        var result = service.createTag(new CreateTagCommand("Spring Security"));

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getSlug()).isEqualTo("spring-security");
    }

    @Test
    void deleteTagDeletesMappingsBeforeTag() {
        PostTag tag = PostTag.builder().name("java").slug("java").build();
        tag.setId(5L);
        when(tagRepository.findById(5L)).thenReturn(Optional.of(tag));

        service.deleteTag(new DeleteTagCommand(5L));

        verify(tagMappingRepository).deleteByTagId(5L);
        verify(tagRepository).delete(tag);
    }
}
