package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.CommentRepository;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.web.mapper.CommentMapper;
import com.certimaster.engagement.adapter.web.response.CommentResponse;
import com.certimaster.engagement.domain.entity.Comment;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final PostWriteRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentCommandService(CommentRepository commentRepository,
                                 PostWriteRepository postRepository,
                                 CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    public CommentResponse create(CreateCommentCommand command) {
        if (!postRepository.existsById(command.postId())) {
            throw ResourceNotFoundException.byId("Post", command.postId());
        }

        if (command.parentId() != null) {
            Comment parent = commentRepository.findById(command.parentId())
                    .orElseThrow(() -> ResourceNotFoundException.byId("Comment", command.parentId()));
            if (!parent.getPostId().equals(command.postId())) {
                throw new BusinessException("INVALID_PARENT_COMMENT",
                        "Parent comment does not belong to this post");
            }
        }

        Comment comment = Comment.builder()
                .postId(command.postId())
                .parentId(command.parentId())
                .authorId(command.authorId())
                .authorName(command.authorName())
                .content(command.content())
                .build();

        Comment saved = commentRepository.save(comment);
        postRepository.incrementCommentsCount(command.postId());
        return commentMapper.toResponse(saved);
    }

    public CommentResponse update(UpdateCommentCommand command) {
        Comment comment = loadOwned(command.commentId(), command.postId(), command.userId(), command.admin());
        comment.setContent(command.content());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    public void delete(DeleteCommentCommand command) {
        Comment comment = loadOwned(command.commentId(), command.postId(), command.userId(), command.admin());

        List<Comment> toDelete = collectWithDescendants(comment);
        commentRepository.deleteAll(toDelete);
        postRepository.decrementCommentsCount(command.postId(), toDelete.size());
    }

    private Comment loadOwned(Long commentId, Long postId, Long userId, boolean admin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> ResourceNotFoundException.byId("Comment", commentId));
        if (!comment.getPostId().equals(postId)) {
            throw ResourceNotFoundException.byId("Comment", commentId);
        }
        if (!admin && !comment.getAuthorId().equals(userId)) {
            throw ForbiddenException.resourceOwnerOnly();
        }
        return comment;
    }

    /** Breadth-first collection of a comment and all of its (transitive) replies. */
    private List<Comment> collectWithDescendants(Comment root) {
        List<Comment> all = new ArrayList<>();
        all.add(root);
        List<Long> frontier = List.of(root.getId());
        while (!frontier.isEmpty()) {
            List<Comment> children = commentRepository.findByParentIdIn(frontier);
            if (children.isEmpty()) {
                break;
            }
            all.addAll(children);
            frontier = children.stream().map(Comment::getId).toList();
        }
        return all;
    }
}
