package com.certimaster.engagement.adapter.web.mapper;

import com.certimaster.engagement.adapter.web.response.CommentResponse;
import com.certimaster.engagement.domain.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommentMapper {

    /** Flat single-comment mapping (no replies attached). */
    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentResponse r = new CommentResponse();
        r.setId(comment.getId());
        r.setContent(comment.getContent());
        r.setAuthorId(comment.getAuthorId());
        r.setAuthorName(comment.getAuthorName());
        r.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        r.setReplies(new ArrayList<>());
        return r;
    }

    /**
     * Build a threaded tree from a flat list (any depth). Top-level comments are
     * returned newest-first; replies stay oldest-first to read as a conversation.
     */
    public List<CommentResponse> toTree(List<Comment> comments) {
        Map<Long, CommentResponse> byId = new LinkedHashMap<>();
        for (Comment c : comments) {
            byId.put(c.getId(), toResponse(c));
        }

        List<CommentResponse> roots = new ArrayList<>();
        for (Comment c : comments) {
            CommentResponse node = byId.get(c.getId());
            CommentResponse parent = c.getParentId() != null ? byId.get(c.getParentId()) : null;
            if (parent != null) {
                parent.getReplies().add(node);
            } else {
                roots.add(node);
            }
        }

        roots.sort(Comparator.comparing(CommentResponse::getCreatedAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return roots;
    }
}
