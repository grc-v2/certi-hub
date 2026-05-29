package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.adapter.persistence.QuestionTagRepository;
import com.certimaster.learning.adapter.persistence.TagReadRepository;
import com.certimaster.learning.adapter.web.mapper.TagMapper;
import com.certimaster.learning.adapter.web.response.TagResponse;
import com.certimaster.learning.domain.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagQueryService {

    private static final Logger log = LoggerFactory.getLogger(TagQueryService.class);

    private static final String TAG = "Tag";

    private final TagReadRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final TagMapper tagMapper;

    public TagQueryService(TagReadRepository tagRepository,
                           QuestionTagRepository questionTagRepository,
                           TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.tagMapper = tagMapper;
    }

    public PageResponse<TagResponse> search(SearchTagsQuery query) {
        log.debug("Searching tags with criteria: {}", query);

        Sort sort = createSort(query.sortBy(), query.sortDirection());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        Page<Tag> page = tagRepository.search(query.keyword(), query.status(), pageable);
        Page<TagResponse> responsePage = page.map(tagMapper::toResponse);
        return PageResponse.of(responsePage);
    }

    public List<TagResponse> getAll() {
        log.debug("Getting all tags");

        return tagRepository.findAllActive().stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    public TagResponse getById(GetTagByIdQuery query) {
        log.debug("Getting tag by id: {}", query.id());

        Tag tag = tagRepository.findActiveById(query.id())
                .orElseThrow(() -> ResourceNotFoundException.byId(TAG, query.id()));
        return tagMapper.toResponse(tag);
    }

    public List<TagResponse> getByQuestion(GetTagsByQuestionQuery query) {
        log.debug("Getting tags by question id: {}", query.questionId());

        List<Tag> tags = questionTagRepository.findTagsByQuestionId(query.questionId());
        return tags.stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
