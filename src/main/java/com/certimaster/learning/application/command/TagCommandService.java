package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.adapter.persistence.QuestionWriteRepository;
import com.certimaster.learning.adapter.persistence.QuestionTagRepository;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionTag;
import com.certimaster.learning.adapter.persistence.TagWriteRepository;
import com.certimaster.learning.adapter.web.mapper.TagMapper;
import com.certimaster.learning.adapter.web.response.TagResponse;
import com.certimaster.learning.domain.entity.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagCommandService {


    private static final String TAG = "Tag";
    private static final String QUESTION = "Question";
    private static final String DELETED_STATUS = "DELETED";

    private final TagWriteRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final QuestionWriteRepository questionRepository;
    private final TagMapper tagMapper;

    public TagCommandService(TagWriteRepository tagRepository,
                             QuestionTagRepository questionTagRepository,
                             QuestionWriteRepository questionRepository,
                             TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.questionRepository = questionRepository;
        this.tagMapper = tagMapper;
    }

    public TagResponse create(CreateTagCommand command) {
        validateNameUnique(command.name(), null);

        Tag tag = tagMapper.toEntity(command);
        if (tag.getStatus() == null) {
            tag.setStatus("ACTIVE");
        }
        Tag saved = tagRepository.save(tag);

        return tagMapper.toResponse(saved);
    }

    public TagResponse update(UpdateTagCommand command) {
        Tag tag = findOrThrow(command.id());
        validateNameUnique(command.name(), command.id());

        tagMapper.updateEntity(tag, command);
        Tag saved = tagRepository.save(tag);

        return tagMapper.toResponse(saved);
    }

    public void delete(DeleteTagCommand command) {
        Tag tag = findActiveOrThrow(command.id());
        tag.setStatus(DELETED_STATUS);
        tagRepository.save(tag);

    }

    public void addTagsToQuestion(AddTagsToQuestionCommand command) {
        Question question = questionRepository.findById(command.questionId())
                .orElseThrow(() -> ResourceNotFoundException.byId(QUESTION, command.questionId()));

        List<Tag> tags = tagRepository.findByIdIn(command.tagIds());
        if (tags.size() != command.tagIds().size()) {
            throw BusinessException.invalidInput("Some tags were not found");
        }

        for (Tag tag : tags) {
            if (!questionTagRepository.existsByQuestionIdAndTagId(command.questionId(), tag.getId())) {
                QuestionTag questionTag = QuestionTag.builder()
                        .question(question)
                        .tag(tag)
                        .build();
                questionTagRepository.save(questionTag);
            }
        }

    }

    public void removeTagsFromQuestion(RemoveTagsFromQuestionCommand command) {
        List<QuestionTag> questionTags = questionTagRepository.findByQuestionId(command.questionId());
        List<QuestionTag> toRemove = questionTags.stream()
                .filter(questionTag -> command.tagIds().contains(questionTag.getTag().getId()))
                .toList();

        questionTagRepository.deleteAll(toRemove);
    }

    private Tag findOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(TAG, id));
    }

    private Tag findActiveOrThrow(Long id) {
        return tagRepository.findActiveById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(TAG, id));
    }

    private void validateNameUnique(String name, Long excludeId) {
        boolean exists = excludeId == null
                ? tagRepository.existsByNameIgnoreCase(name)
                : tagRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);

        if (exists) {
            throw BusinessException.duplicateResource(TAG, name);
        }
    }
}
