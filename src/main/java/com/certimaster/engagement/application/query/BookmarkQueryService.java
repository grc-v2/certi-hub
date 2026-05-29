package com.certimaster.engagement.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.engagement.adapter.persistence.BookmarkRepository;
import com.certimaster.engagement.adapter.web.mapper.BookmarkMapper;
import com.certimaster.engagement.adapter.web.response.BookmarkResponse;
import com.certimaster.engagement.domain.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookmarkQueryService {

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;

    public BookmarkQueryService(BookmarkRepository bookmarkRepository,
                                BookmarkMapper bookmarkMapper) {
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkMapper = bookmarkMapper;
    }

    public boolean isBookmarked(IsPostBookmarkedQuery query) {
        return bookmarkRepository.existsByUserIdAndPostId(query.userId(), query.postId());
    }

    public PageResponse<BookmarkResponse> getUserBookmarks(GetUserBookmarksQuery query) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserId(
                query.userId(),
                PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return PageResponse.of(bookmarks.map(bookmarkMapper::toResponse));
    }
}
