package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.adapter.persistence.CertificationDomainWriteRepository;
import com.certimaster.learning.application.command.CreateQuestionCommand;
import com.certimaster.learning.application.command.DeleteQuestionCommand;
import com.certimaster.learning.application.command.QuestionOptionCommand;
import com.certimaster.learning.application.command.UpdateQuestionCommand;
import com.certimaster.learning.application.command.UpdateQuestionStatusCommand;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionOption;
import com.certimaster.learning.adapter.web.mapper.QuestionMapper;
import com.certimaster.learning.adapter.persistence.QuestionOptionRepository;
import com.certimaster.learning.adapter.persistence.QuestionWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionCommandService {

    private static final Logger log = LoggerFactory.getLogger(QuestionCommandService.class);

    private static final String QUESTION = "Question";
    private static final String DOMAIN = "CertificationDomain";

    private final QuestionWriteRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final CertificationDomainWriteRepository domainRepository;
    private final QuestionMapper questionMapper;

    public QuestionCommandService(QuestionWriteRepository questionRepository,
                                  QuestionOptionRepository questionOptionRepository,
                                  CertificationDomainWriteRepository domainRepository,
                                  QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.domainRepository = domainRepository;
        this.questionMapper = questionMapper;
    }

    public QuestionResponse create(CreateQuestionCommand command) {
        log.debug("Creating question for domain: {}", command.domainId());

        CertificationDomain domain = findDomainOrThrow(command.domainId());
        validateOptions(command.type(), command.options());

        Question question = questionMapper.toEntity(command);
        question.setDomain(domain);

        Question saved = questionRepository.save(question);
        createOptions(saved, command.options());

        log.info("Created question with id: {}", saved.getId());
        return questionMapper.toResponse(saved);
    }

    public QuestionResponse update(UpdateQuestionCommand command) {
        log.debug("Updating question with id: {}", command.id());

        Question question = findOrThrow(command.id());
        validateOptions(command.type(), command.options());

        if (!question.getDomain().getId().equals(command.domainId())) {
            CertificationDomain domain = findDomainOrThrow(command.domainId());
            question.setDomain(domain);
        }

        questionMapper.updateEntity(question, command);

        questionOptionRepository.deleteByQuestionId(command.id());
        question.getQuestionOptions().clear();
        createOptions(question, command.options());

        Question saved = questionRepository.save(question);
        log.info("Updated question with id: {}", saved.getId());

        return questionMapper.toResponse(saved);
    }

    public QuestionResponse updateStatus(UpdateQuestionStatusCommand command) {
        log.debug("Updating question {} status to {}", command.id(), command.status());

        if (command.status() == null || command.status().isBlank()) {
            throw BusinessException.invalidInput("Status is required");
        }
        String normalized = command.status().trim().toUpperCase();
        switch (normalized) {
            case "DRAFT":
            case "REVIEW":
            case "PUBLISHED":
            case "DEPRECATED":
            case "REPORTED":
                break;
            default:
                throw BusinessException.invalidInput("Invalid status: " + command.status());
        }

        Question question = findOrThrow(command.id());
        String previousStatus = question.getStatus();
        question.setStatus(normalized);

        if ("PUBLISHED".equals(normalized) && question.getPublishedAt() == null) {
            question.setPublishedAt(java.time.LocalDateTime.now());
        }

        Question saved = questionRepository.save(question);
        log.info("Question {} status changed: {} -> {}", command.id(), previousStatus, normalized);
        return questionMapper.toResponse(saved);
    }

    public void delete(DeleteQuestionCommand command) {
        log.debug("Deleting question with id: {}", command.id());
        Question question = findOrThrow(command.id());
        questionRepository.delete(question);
        log.info("Deleted question with id: {}", command.id());
    }

    private Question findOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(QUESTION, id));
    }

    private CertificationDomain findDomainOrThrow(Long domainId) {
        return domainRepository.findById(domainId)
                .orElseThrow(() -> ResourceNotFoundException.byId(DOMAIN, domainId));
    }

    private void validateOptions(String type, List<QuestionOptionCommand> options) {
        boolean hasCorrect = options.stream().anyMatch(QuestionOptionCommand::isCorrect);
        if (!hasCorrect) {
            throw BusinessException.invalidInput("At least one option must be marked as correct");
        }

        long correctCount = options.stream().filter(QuestionOptionCommand::isCorrect).count();

        if ("SINGLE_CHOICE".equals(type) && correctCount != 1) {
            throw BusinessException.invalidInput("Single choice question must have exactly one correct answer");
        }

        if ("TRUE_FALSE".equals(type)) {
            if (options.size() != 2) {
                throw BusinessException.invalidInput("True/False question must have exactly 2 options");
            }
            if (correctCount != 1) {
                throw BusinessException.invalidInput("True/False question must have exactly one correct answer");
            }
        }
    }

    private void createOptions(Question question, List<QuestionOptionCommand> optionCommands) {
        for (QuestionOptionCommand optionCommand : optionCommands) {
            QuestionOption option = QuestionOption.builder()
                    .question(question)
                    .content(optionCommand.content())
                    .isCorrect(optionCommand.isCorrect())
                    .orderIndex(optionCommand.orderIndex())
                    .build();
            question.getQuestionOptions().add(option);
        }
    }
}
