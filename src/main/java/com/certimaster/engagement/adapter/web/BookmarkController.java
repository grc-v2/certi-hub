package com.certimaster.engagement.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.engagement.application.command.BookmarkCommandService;
import com.certimaster.engagement.application.command.ToggleBookmarkCommand;
import com.certimaster.engagement.application.query.BookmarkQueryService;
import com.certimaster.engagement.application.query.GetUserBookmarksQuery;
import com.certimaster.engagement.adapter.web.request.BookmarkRequest;
import com.certimaster.engagement.adapter.web.response.BookmarkResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final BookmarkCommandService bookmarkCommandService;
    private final BookmarkQueryService bookmarkQueryService;

    public BookmarkController(BookmarkCommandService bookmarkCommandService,
                              BookmarkQueryService bookmarkQueryService) {
        this.bookmarkCommandService = bookmarkCommandService;
        this.bookmarkQueryService = bookmarkQueryService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<BookmarkResponse> toggleBookmark(
            @PathVariable Long postId,
            @RequestBody(required = false) BookmarkRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String notes = request != null ? request.getNotes() : null;
        BookmarkResponse result = bookmarkCommandService.toggleBookmark(new ToggleBookmarkCommand(postId, userId, notes));
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookmarkResponse>> getUserBookmarks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(bookmarkQueryService.getUserBookmarks(new GetUserBookmarksQuery(userId, page, size)));
    }
}
