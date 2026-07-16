package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.CommentRepository;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.web.mapper.CommentMapper;
import com.certimaster.engagement.domain.entity.Comment;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostWriteRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentCommandService service;

    private Comment comment(Long id, Long postId, Long parentId, Long authorId) {
        Comment c = Comment.builder().postId(postId).parentId(parentId)
                .authorId(authorId).content("nội dung").build();
        c.setId(id);
        return c;
    }

    @Test
    void createTopLevelCommentSavesAndIncrementsCounter() {
        when(postRepository.existsById(10L)).thenReturn(true);
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateCommentCommand(10L, null, 42L, "Tan", "Bài hay quá"));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();
        assertThat(saved.getPostId()).isEqualTo(10L);
        assertThat(saved.getParentId()).isNull();
        assertThat(saved.getAuthorId()).isEqualTo(42L);
        assertThat(saved.getAuthorName()).isEqualTo("Tan");
        assertThat(saved.getContent()).isEqualTo("Bài hay quá");
        verify(postRepository).incrementCommentsCount(10L);
    }

    @Test
    void createReplyLinksToParentOnSamePost() {
        when(postRepository.existsById(10L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment(1L, 10L, null, 7L)));
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateCommentCommand(10L, 1L, 42L, "Tan", "reply"));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertThat(captor.getValue().getParentId()).isEqualTo(1L);
    }

    @Test
    void createReplyWithParentFromAnotherPostFails() {
        when(postRepository.existsById(10L)).thenReturn(true);
        // Parent comment lives on post 99, not post 10.
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment(1L, 99L, null, 7L)));

        assertThatThrownBy(() -> service.create(new CreateCommentCommand(10L, 1L, 42L, "Tan", "reply")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Parent comment does not belong to this post");

        verify(commentRepository, never()).save(any());
        verify(postRepository, never()).incrementCommentsCount(any());
    }

    @Test
    void createOnMissingPostFails() {
        when(postRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> service.create(new CreateCommentCommand(10L, null, 42L, "Tan", "x")))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateByOwnerChangesContent() {
        Comment existing = comment(1L, 10L, null, 42L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(commentRepository.save(existing)).thenReturn(existing);

        service.update(new UpdateCommentCommand(1L, 10L, 42L, false, "đã sửa"));

        assertThat(existing.getContent()).isEqualTo("đã sửa");
    }

    @Test
    void updateByAnotherUserIsForbidden() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment(1L, 10L, null, 42L)));

        assertThatThrownBy(() -> service.update(new UpdateCommentCommand(1L, 10L, 7L, false, "hack")))
                .isInstanceOf(ForbiddenException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void adminCanUpdateAnyComment() {
        Comment existing = comment(1L, 10L, null, 42L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(commentRepository.save(existing)).thenReturn(existing);

        service.update(new UpdateCommentCommand(1L, 10L, 7L, true, "moderated"));

        assertThat(existing.getContent()).isEqualTo("moderated");
    }

    @Test
    void updateWithMismatchedPostIdFails() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment(1L, 99L, null, 42L)));

        assertThatThrownBy(() -> service.update(new UpdateCommentCommand(1L, 10L, 42L, false, "x")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteRemovesCommentWithAllDescendantsAndDecrementsCounter() {
        Comment root = comment(1L, 10L, null, 42L);
        Comment childA = comment(2L, 10L, 1L, 7L);
        Comment childB = comment(3L, 10L, 1L, 8L);
        Comment grandchild = comment(4L, 10L, 2L, 9L);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(root));
        when(commentRepository.findByParentIdIn(List.of(1L))).thenReturn(List.of(childA, childB));
        when(commentRepository.findByParentIdIn(List.of(2L, 3L))).thenReturn(List.of(grandchild));
        when(commentRepository.findByParentIdIn(List.of(4L))).thenReturn(List.of());

        service.delete(new DeleteCommentCommand(1L, 10L, 42L, false));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Comment>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(commentRepository).deleteAll(captor.capture());
        assertThat(captor.getValue()).containsExactly(root, childA, childB, grandchild);
        verify(postRepository).decrementCommentsCount(10L, 4);
    }

    @Test
    void deleteByNonOwnerWithoutAdminIsForbidden() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment(1L, 10L, null, 42L)));

        assertThatThrownBy(() -> service.delete(new DeleteCommentCommand(1L, 10L, 7L, false)))
                .isInstanceOf(ForbiddenException.class);
        verify(commentRepository, never()).deleteAll(anyList());
    }
}
