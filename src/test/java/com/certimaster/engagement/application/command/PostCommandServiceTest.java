package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.web.mapper.PostMapper;
import com.certimaster.engagement.domain.event.PostPublishedEvent;
import com.certimaster.engagement.domain.service.ProcessedContent;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.domain.service.PostContentProcessor;
import com.certimaster.engagement.application.command.AssignTagsToPostCommand;
import com.certimaster.engagement.application.command.TagCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCommandServiceTest {

    @Mock
    private PostWriteRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private SlugService slugService;

    @Mock
    private PostContentProcessor contentProcessor;

    @Mock
    private TagCommandService tagCommandService;

    @Mock
    private ApplicationEventPublisher applicationEvents;

    @InjectMocks
    private PostCommandService service;

    @Test
    void createPostCreatesDraftAndAssignsTags() {
        CreatePostCommand command = new CreatePostCommand(
                "Java CQRS",
                "content",
                null,
                null,
                null,
                List.of("java", "cqrs"),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                42L
        );
        Post post = new Post();
        Post saved = new Post();
        saved.setId(10L);
        saved.setSlug("java-cqrs");

        when(postMapper.toEntity(command)).thenReturn(post);
        when(slugService.generateUniqueSlug("Java CQRS")).thenReturn("java-cqrs");
        when(contentProcessor.process("content")).thenReturn(new ProcessedContent("<p>content</p>", "[]", 1, 1, "content"));
        when(postRepository.save(post)).thenReturn(saved);

        service.createPost(command);

        assertThat(post.getAuthorId()).isEqualTo(42L);
        assertThat(post.getSlug()).isEqualTo("java-cqrs");
        assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
        verify(tagCommandService).assignTagsToPost(new AssignTagsToPostCommand(10L, List.of("java", "cqrs")));
    }

    @Test
    void publishPostChangesStatusAndPublishedAt() {
        Post post = new Post();
        post.setId(10L);
        post.setAuthorId(42L);
        post.setStatus(PostStatus.DRAFT);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        service.publishPost(new PublishPostCommand(10L, 42L));

        ArgumentCaptor<Post> savedPost = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(savedPost.capture());
        verify(applicationEvents).publishEvent(any(PostPublishedEvent.class));
        assertThat(savedPost.getValue().getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(savedPost.getValue().getPublishedAt()).isNotNull();
    }

    private Post post(Long id, Long authorId, PostStatus status) {
        Post post = new Post();
        post.setId(id);
        post.setAuthorId(authorId);
        post.setStatus(status);
        return post;
    }

    @Test
    void publishByNonAuthorIsForbidden() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.DRAFT)));

        assertThatThrownBy(() -> service.publishPost(new PublishPostCommand(10L, 7L)))
                .isInstanceOf(ForbiddenException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void publishAlreadyPublishedPostFails() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.PUBLISHED)));

        assertThatThrownBy(() -> service.publishPost(new PublishPostCommand(10L, 42L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already published");
        verify(postRepository, never()).save(any());
    }

    @Test
    void publishArchivedPostRequiresRestoreFirst() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.ARCHIVED)));

        assertThatThrownBy(() -> service.publishPost(new PublishPostCommand(10L, 42L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restore it first");
        verify(postRepository, never()).save(any());
    }

    @Test
    void publishingScheduledPostClearsScheduledPublishAt() {
        Post post = post(10L, 42L, PostStatus.SCHEDULED);
        post.setScheduledPublishAt(LocalDateTime.now().plusDays(1));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        service.publishPost(new PublishPostCommand(10L, 42L));

        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(post.getScheduledPublishAt()).isNull();
        assertThat(post.getPublishedAt()).isNotNull();
    }

    @Test
    void adminPublishBypassesOwnershipCheck() {
        Post post = post(10L, 42L, PostStatus.IN_REVIEW);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        service.adminPublishPost(10L);

        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
        verify(applicationEvents).publishEvent(any(PostPublishedEvent.class));
    }

    @Test
    void schedulePostSetsStatusAndFutureTime() {
        Post post = post(10L, 42L, PostStatus.DRAFT);
        LocalDateTime at = LocalDateTime.now().plusDays(2);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        service.schedulePost(new SchedulePostCommand(10L, at, 42L));

        assertThat(post.getStatus()).isEqualTo(PostStatus.SCHEDULED);
        assertThat(post.getScheduledPublishAt()).isEqualTo(at);
    }

    @Test
    void scheduleInThePastFails() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.DRAFT)));

        assertThatThrownBy(() -> service.schedulePost(
                new SchedulePostCommand(10L, LocalDateTime.now().minusMinutes(1), 42L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must be in the future");
        verify(postRepository, never()).save(any());
    }

    @Test
    void scheduleAlreadyPublishedPostFails() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.PUBLISHED)));

        assertThatThrownBy(() -> service.schedulePost(
                new SchedulePostCommand(10L, LocalDateTime.now().plusDays(1), 42L)))
                .isInstanceOf(BusinessException.class);
        verify(postRepository, never()).save(any());
    }

    @Test
    void scheduleArchivedPostIsRejected() {
        // Scheduling must not bypass the restore requirement that direct publish enforces.
        Post post = post(10L, 42L, PostStatus.ARCHIVED);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> service.schedulePost(
                new SchedulePostCommand(10L, LocalDateTime.now().plusDays(1), 42L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("archived");

        assertThat(post.getStatus()).isEqualTo(PostStatus.ARCHIVED);
        verify(postRepository, never()).save(any());
    }

    @Test
    void submitForReviewMovesDraftToInReview() {
        Post post = post(10L, 42L, PostStatus.DRAFT);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        service.submitForReview(new SubmitPostForReviewCommand(10L, 42L));

        assertThat(post.getStatus()).isEqualTo(PostStatus.IN_REVIEW);
    }

    @Test
    void submitForReviewFromNonDraftFails() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.PUBLISHED)));

        assertThatThrownBy(() -> service.submitForReview(new SubmitPostForReviewCommand(10L, 42L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only draft posts");
        verify(postRepository, never()).save(any());
    }

    @Test
    void deleteByNonAuthorIsForbidden() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post(10L, 42L, PostStatus.DRAFT)));

        assertThatThrownBy(() -> service.deletePost(new DeletePostCommand(10L, 7L)))
                .isInstanceOf(ForbiddenException.class);
        verify(postRepository, never()).delete(any(Post.class));
    }
}
