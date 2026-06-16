package com.certimaster.engagement.adapter.web;

import com.certimaster.engagement.adapter.web.request.CommentRequest;
import com.certimaster.engagement.adapter.web.response.CommentResponse;
import com.certimaster.engagement.application.command.CommentCommandService;
import com.certimaster.engagement.application.command.CreateCommentCommand;
import com.certimaster.engagement.application.command.DeleteCommentCommand;
import com.certimaster.engagement.application.command.UpdateCommentCommand;
import com.certimaster.engagement.application.query.CommentQueryService;
import com.certimaster.engagement.application.query.GetPostCommentsQuery;
import com.certimaster.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    public CommentController(CommentCommandService commentCommandService,
                             CommentQueryService commentQueryService) {
        this.commentCommandService = commentCommandService;
        this.commentQueryService = commentQueryService;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentQueryService.getPostComments(new GetPostCommentsQuery(postId)));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                      @Valid @RequestBody CommentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String authorName = SecurityUtils.getCurrentUsername();
        CommentResponse created = commentCommandService.create(
                new CreateCommentCommand(postId, request.getParentId(), userId, authorName, request.getContent()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long postId,
                                                         @PathVariable Long commentId,
                                                         @Valid @RequestBody CommentRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CommentResponse updated = commentCommandService.update(
                new UpdateCommentCommand(commentId, postId, userId, SecurityUtils.isAdmin(), request.getContent()));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        commentCommandService.delete(new DeleteCommentCommand(commentId, postId, userId, SecurityUtils.isAdmin()));
        return ResponseEntity.noContent().build();
    }
}
