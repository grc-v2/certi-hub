package com.certimaster.engagement.application.query;

import com.certimaster.engagement.adapter.persistence.CommentRepository;
import com.certimaster.engagement.adapter.web.mapper.CommentMapper;
import com.certimaster.engagement.adapter.web.response.CommentResponse;
import com.certimaster.engagement.domain.entity.Comment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentQueryService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentResponse> getPostComments(GetPostCommentsQuery query) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(query.postId());
        return commentMapper.toTree(comments);
    }
}
