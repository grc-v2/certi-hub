package com.certimaster.learning.adapter.web;

import com.certimaster.learning.application.command.QuestionBulkImportService;
import com.certimaster.learning.adapter.web.request.BulkQuestionRequest;
import com.certimaster.learning.application.command.BulkImportQuestionsCommand;
import com.certimaster.learning.application.command.BulkQuestionCommand;
import com.certimaster.learning.application.command.CreateQuestionCommand;
import com.certimaster.learning.application.command.DeleteQuestionCommand;
import com.certimaster.learning.application.command.QuestionOptionCommand;
import com.certimaster.learning.application.command.UpdateQuestionCommand;
import com.certimaster.learning.application.query.GetFlashcardQuestionsQuery;
import com.certimaster.learning.application.query.GetQuestionByIdQuery;
import com.certimaster.learning.application.query.GetQuestionForExamQuery;
import com.certimaster.learning.application.query.GetQuestionsByDomainQuery;
import com.certimaster.learning.application.query.GetQuestionsByExamQuery;
import com.certimaster.learning.adapter.web.request.FlashcardQuestionRequest;
import com.certimaster.learning.adapter.web.request.QuestionOptionRequest;
import com.certimaster.learning.adapter.web.request.QuestionWithOptionsRequest;
import com.certimaster.learning.adapter.web.response.BulkImportResponse;
import com.certimaster.learning.adapter.web.response.ExamQuestionsGroupedResponse;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.application.command.QuestionCommandService;
import com.certimaster.learning.application.query.QuestionQueryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionCommandService questionCommandService;
    private final QuestionQueryService questionQueryService;
    private final QuestionBulkImportService bulkImportService;

    public QuestionController(QuestionCommandService questionCommandService,
                               QuestionQueryService questionQueryService,
                               QuestionBulkImportService bulkImportService) {
        this.questionCommandService = questionCommandService;
        this.questionQueryService = questionQueryService;
        this.bulkImportService = bulkImportService;
    }

    @GetMapping
    public ResponseEntity<ExamQuestionsGroupedResponse> getQuestionsByExam(@RequestParam Long examId) {
        return ResponseEntity.ok(questionQueryService.getQuestionsByExamId(new GetQuestionsByExamQuery(examId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(questionQueryService.getById(new GetQuestionByIdQuery(id)));
    }

    @GetMapping("/{id}/exam")
    public ResponseEntity<QuestionResponse> getByIdForExam(@PathVariable Long id) {
        return ResponseEntity.ok(questionQueryService.getByIdForExam(new GetQuestionForExamQuery(id)));
    }

    @GetMapping("/domain/{domainId}")
    public ResponseEntity<List<QuestionResponse>> getByDomainId(@PathVariable Long domainId) {
        return ResponseEntity.ok(questionQueryService.getByDomainId(new GetQuestionsByDomainQuery(domainId)));
    }

    @PostMapping("/flashcard")
    public ResponseEntity<List<QuestionResponse>> getFlashcardQuestions(
            @Valid @RequestBody FlashcardQuestionRequest request) {
        return ResponseEntity.ok(questionQueryService.getFlashcardQuestions(new GetFlashcardQuestionsQuery(
                request.getCertificationId(),
                request.getDomainIds(),
                request.getDifficulties(),
                request.getCount()
        )));
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionWithOptionsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionCommandService.create(toCreateCommand(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody QuestionWithOptionsRequest request) {
        return ResponseEntity.ok(questionCommandService.update(toUpdateCommand(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionCommandService.delete(new DeleteQuestionCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-import")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<BulkImportResponse> bulkImport(
            @RequestParam Long certificationId,
            @RequestParam Long domainId,
            @Valid @RequestBody List<BulkQuestionRequest> questions) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bulkImportService.bulkImport(toBulkImportCommand(certificationId, domainId, questions)));
    }

    private CreateQuestionCommand toCreateCommand(QuestionWithOptionsRequest request) {
        return new CreateQuestionCommand(
                request.getDomainId(),
                request.getType(),
                request.getContent(),
                request.getExplanation(),
                request.getDifficulty(),
                request.getPoints(),
                request.getTimeLimitSeconds(),
                request.getReferenceUrl(),
                toOptionCommands(request.getOptions())
        );
    }

    private UpdateQuestionCommand toUpdateCommand(Long id, QuestionWithOptionsRequest request) {
        return new UpdateQuestionCommand(
                id,
                request.getDomainId(),
                request.getType(),
                request.getContent(),
                request.getExplanation(),
                request.getDifficulty(),
                request.getPoints(),
                request.getTimeLimitSeconds(),
                request.getReferenceUrl(),
                toOptionCommands(request.getOptions())
        );
    }

    private List<QuestionOptionCommand> toOptionCommands(List<QuestionOptionRequest> options) {
        if (options == null) {
            return List.of();
        }
        return options.stream()
                .map(option -> new QuestionOptionCommand(
                        option.getContent(),
                        option.getIsCorrect(),
                        option.getOrderIndex()
                ))
                .toList();
    }

    private BulkImportQuestionsCommand toBulkImportCommand(Long certificationId, Long domainId, List<BulkQuestionRequest> questions) {
        return new BulkImportQuestionsCommand(
                certificationId,
                domainId,
                questions.stream()
                        .map(question -> new BulkQuestionCommand(
                                question.getQuestionText(),
                                question.getDifficulty(),
                                question.getQuestionType(),
                                question.getExplanation(),
                                toOptionCommands(question.getOptions())
                        ))
                        .toList()
        );
    }
}
