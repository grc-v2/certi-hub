package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.external.WebPageTitleFetcher;
import com.certimaster.productivity.adapter.persistence.TrackerResourceRepository;
import com.certimaster.productivity.adapter.web.mapper.ResourceMapper;
import com.certimaster.productivity.adapter.web.response.ResourceResponse;
import com.certimaster.productivity.domain.entity.ResourceKind;
import com.certimaster.productivity.domain.entity.TrackerResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceCommandServiceTest {

    @Mock
    private TrackerResourceRepository resourceRepository;

    @Mock
    private WebPageTitleFetcher titleFetcher;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private ResourceCommandService service;

    @Test
    void createLinkDetectsKindAndFetchesTitleWhenBlank() {
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());
        when(titleFetcher.fetchTitle("https://github.com/anthropics/claude")).thenReturn("anthropics/claude");

        service.create(new CreateResourceCommand(7L, "https://github.com/anthropics/claude", null, null, null, null));

        TrackerResource saved = captureSaved();
        assertThat(saved.getKind()).isEqualTo(ResourceKind.GITHUB);
        assertThat(saved.getTitle()).isEqualTo("anthropics/claude");
        assertThat(saved.getUrl()).isEqualTo("https://github.com/anthropics/claude");
        assertThat(saved.getUserId()).isEqualTo(7L);
    }

    @Test
    void createLinkKeepsProvidedTitleWithoutFetching() {
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());

        service.create(new CreateResourceCommand(1L, "https://youtu.be/xyz", "My title", null, null, null));

        TrackerResource saved = captureSaved();
        assertThat(saved.getKind()).isEqualTo(ResourceKind.YOUTUBE);
        assertThat(saved.getTitle()).isEqualTo("My title");
        verify(titleFetcher, never()).fetchTitle(any());
    }

    @Test
    void createNoteWithoutUrlDerivesTitleFromContent() {
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());

        service.create(new CreateResourceCommand(1L, null, null, "Mua sữa\nĐón con", null, null));

        TrackerResource saved = captureSaved();
        assertThat(saved.getKind()).isEqualTo(ResourceKind.NOTE);
        assertThat(saved.getUrl()).isNull();
        assertThat(saved.getContent()).isEqualTo("Mua sữa\nĐón con");
        assertThat(saved.getTitle()).isEqualTo("Mua sữa");
        verify(titleFetcher, never()).fetchTitle(any());
    }

    private TrackerResource captureSaved() {
        ArgumentCaptor<TrackerResource> captor = ArgumentCaptor.forClass(TrackerResource.class);
        verify(resourceRepository).save(captor.capture());
        return captor.getValue();
    }
}
