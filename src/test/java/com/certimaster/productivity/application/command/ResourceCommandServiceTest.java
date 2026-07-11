package com.certimaster.productivity.application.command;

import com.certimaster.learning.application.lookup.CertificationRefLookup;
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

import com.certimaster.shared.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private CertificationRefLookup certificationLookup;

    @InjectMocks
    private ResourceCommandService service;

    @Test
    void createLinkDetectsKindAndFetchesTitleWhenBlank() {
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());
        when(titleFetcher.fetchTitle("https://github.com/anthropics/claude")).thenReturn("anthropics/claude");

        service.create(new CreateResourceCommand(7L, "https://github.com/anthropics/claude", null, null, null, null, null));

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

        service.create(new CreateResourceCommand(1L, "https://youtu.be/xyz", "My title", null, null, null, null));

        TrackerResource saved = captureSaved();
        assertThat(saved.getKind()).isEqualTo(ResourceKind.YOUTUBE);
        assertThat(saved.getTitle()).isEqualTo("My title");
        verify(titleFetcher, never()).fetchTitle(any());
    }

    @Test
    void createNoteWithoutUrlDerivesTitleFromContent() {
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());

        service.create(new CreateResourceCommand(1L, null, null, "Mua sữa\nĐón con", null, null, null));

        TrackerResource saved = captureSaved();
        assertThat(saved.getKind()).isEqualTo(ResourceKind.NOTE);
        assertThat(saved.getUrl()).isNull();
        assertThat(saved.getContent()).isEqualTo("Mua sữa\nĐón con");
        assertThat(saved.getTitle()).isEqualTo("Mua sữa");
        verify(titleFetcher, never()).fetchTitle(any());
    }

    @Test
    void createWithCertificationValidatesAndStoresIt() {
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());
        when(certificationLookup.existsActiveById(42L)).thenReturn(true);

        service.create(new CreateResourceCommand(1L, null, null, "Ôn thi SAA", null, null, 42L));

        TrackerResource saved = captureSaved();
        assertThat(saved.getCertificationId()).isEqualTo(42L);
    }

    @Test
    void createWithMissingCertificationFails() {
        when(certificationLookup.existsActiveById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.create(
                new CreateResourceCommand(1L, null, null, "Ghi chú", null, null, 99L)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(resourceRepository, never()).save(any());
    }

    @Test
    void setCertificationAttachesValidatedCertification() {
        TrackerResource resource = TrackerResource.builder().userId(1L).kind(ResourceKind.NOTE).build();
        when(resourceRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(resource));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());
        when(certificationLookup.existsActiveById(42L)).thenReturn(true);

        service.setCertification(new SetResourceCertificationCommand(5L, 1L, 42L));

        assertThat(resource.getCertificationId()).isEqualTo(42L);
    }

    @Test
    void setCertificationWithNullDetaches() {
        TrackerResource resource = TrackerResource.builder().userId(1L).kind(ResourceKind.NOTE).certificationId(42L).build();
        when(resourceRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(resource));
        when(resourceMapper.toResponse(any())).thenReturn(new ResourceResponse());

        service.setCertification(new SetResourceCertificationCommand(5L, 1L, null));

        assertThat(resource.getCertificationId()).isNull();
        verify(certificationLookup, never()).existsActiveById(any());
    }

    @Test
    void setCertificationWithMissingCertificationFails() {
        TrackerResource resource = TrackerResource.builder().userId(1L).kind(ResourceKind.NOTE).build();
        when(resourceRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(resource));
        when(certificationLookup.existsActiveById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.setCertification(new SetResourceCertificationCommand(5L, 1L, 99L)))
                .isInstanceOf(ResourceNotFoundException.class);

        assertThat(resource.getCertificationId()).isNull();
    }

    private TrackerResource captureSaved() {
        ArgumentCaptor<TrackerResource> captor = ArgumentCaptor.forClass(TrackerResource.class);
        verify(resourceRepository).save(captor.capture());
        return captor.getValue();
    }
}
