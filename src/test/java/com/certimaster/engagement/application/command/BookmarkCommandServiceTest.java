package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.BookmarkRepository;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.web.mapper.BookmarkMapper;
import com.certimaster.engagement.adapter.web.response.BookmarkResponse;
import com.certimaster.engagement.domain.entity.Bookmark;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkCommandServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private PostWriteRepository postRepository;

    @Mock
    private BookmarkMapper bookmarkMapper;

    @InjectMocks
    private BookmarkCommandService service;

    @Test
    void toggleOnUnbookmarkedPostCreatesBookmarkAndIncrementsCount() {
        Post post = new Post();
        post.setId(10L);
        when(bookmarkRepository.findByUserIdAndPostId(42L, 10L)).thenReturn(Optional.empty());
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookmarkMapper.toResponse(any())).thenReturn(new BookmarkResponse());

        BookmarkResponse response = service.toggleBookmark(new ToggleBookmarkCommand(10L, 42L, "đọc sau"));

        assertThat(response).isNotNull();
        ArgumentCaptor<Bookmark> captor = ArgumentCaptor.forClass(Bookmark.class);
        verify(bookmarkRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(42L);
        assertThat(captor.getValue().getPost()).isEqualTo(post);
        assertThat(captor.getValue().getNotes()).isEqualTo("đọc sau");
        verify(postRepository).updateBookmarkCount(10L, 1);
    }

    @Test
    void toggleOnBookmarkedPostRemovesItAndDecrementsCount() {
        Bookmark existing = Bookmark.builder().userId(42L).build();
        when(bookmarkRepository.findByUserIdAndPostId(42L, 10L)).thenReturn(Optional.of(existing));

        BookmarkResponse response = service.toggleBookmark(new ToggleBookmarkCommand(10L, 42L, null));

        assertThat(response).isNull();
        verify(bookmarkRepository).delete(existing);
        verify(postRepository).updateBookmarkCount(10L, -1);
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void toggleOnMissingPostFails() {
        when(bookmarkRepository.findByUserIdAndPostId(42L, 10L)).thenReturn(Optional.empty());
        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleBookmark(new ToggleBookmarkCommand(10L, 42L, null)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void removeBookmarkDecrementsCountOnlyWhenARowWasDeleted() {
        when(bookmarkRepository.deleteByUserIdAndPostId(42L, 10L)).thenReturn(1L);

        service.removeBookmark(new RemoveBookmarkCommand(10L, 42L));

        verify(postRepository).updateBookmarkCount(10L, -1);
    }

    @Test
    void removeBookmarkOnNeverBookmarkedPostLeavesCountUntouched() {
        when(bookmarkRepository.deleteByUserIdAndPostId(42L, 10L)).thenReturn(0L);

        service.removeBookmark(new RemoveBookmarkCommand(10L, 42L));

        verify(postRepository, never()).updateBookmarkCount(anyLong(), anyInt());
    }
}
