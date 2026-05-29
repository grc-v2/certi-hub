package com.certimaster.engagement.application.command;

import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.engagement.adapter.web.response.BookmarkResponse;
import com.certimaster.engagement.domain.entity.Bookmark;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.adapter.persistence.BookmarkRepository;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.web.mapper.BookmarkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BookmarkCommandService {

    private static final Logger log = LoggerFactory.getLogger(BookmarkCommandService.class);

    private final BookmarkRepository bookmarkRepository;
    private final PostWriteRepository postRepository;
    private final BookmarkMapper bookmarkMapper;

    public BookmarkCommandService(BookmarkRepository bookmarkRepository,
                                  PostWriteRepository postRepository,
                                  BookmarkMapper bookmarkMapper) {
        this.bookmarkRepository = bookmarkRepository;
        this.postRepository = postRepository;
        this.bookmarkMapper = bookmarkMapper;
    }

    public BookmarkResponse toggleBookmark(ToggleBookmarkCommand command) {
        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndPostId(command.userId(), command.postId());
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            postRepository.updateBookmarkCount(command.postId(), -1);
            return null;
        }

        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", command.postId()));

        Bookmark bookmark = Bookmark.builder()
                .userId(command.userId())
                .post(post)
                .notes(command.notes())
                .build();

        Bookmark saved = bookmarkRepository.save(bookmark);
        postRepository.updateBookmarkCount(command.postId(), 1);

        log.info("User {} bookmarked post {}", command.userId(), command.postId());
        return bookmarkMapper.toResponse(saved);
    }

    public void removeBookmark(RemoveBookmarkCommand command) {
        bookmarkRepository.deleteByUserIdAndPostId(command.userId(), command.postId());
        postRepository.updateBookmarkCount(command.postId(), -1);
    }
}
