package com.certimaster.learning.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.learning.application.command.CreateQuestionCommand;
import com.certimaster.learning.application.command.DeleteQuestionCommand;
import com.certimaster.learning.application.command.QuestionOptionCommand;
import com.certimaster.learning.application.command.UpdateQuestionCommand;
import com.certimaster.learning.application.command.UpdateQuestionStatusCommand;
import com.certimaster.learning.application.query.GetQuestionByIdQuery;
import com.certimaster.learning.application.query.SearchQuestionsQuery;
import com.certimaster.learning.adapter.web.request.QuestionOptionRequest;
import com.certimaster.learning.adapter.web.request.QuestionWithOptionsRequest;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.application.command.QuestionCommandService;
import com.certimaster.learning.application.query.QuestionQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuestionController {

    private final QuestionCommandService questionCommandService;
    private final QuestionQueryService questionQueryService;

    public AdminQuestionController(QuestionCommandService questionCommandService,
                                   QuestionQueryService questionQueryService) {
        this.questionCommandService = questionCommandService;
        this.questionQueryService = questionQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<QuestionResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Long certificationId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        SearchQuestionsQuery query = new SearchQuestionsQuery(
                keyword,
                domainId,
                certificationId,
                type,
                difficulty,
                status,
                page,
                size,
                sortBy,
                sortDirection
        );
        return ResponseEntity.ok(questionQueryService.search(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(questionQueryService.getById(new GetQuestionByIdQuery(id)));
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<QuestionResponse> updateStatus(@PathVariable Long id,
                                                          @RequestParam String status) {
        return ResponseEntity.ok(questionCommandService.updateStatus(new UpdateQuestionStatusCommand(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionCommandService.delete(new DeleteQuestionCommand(id));
        return ResponseEntity.noContent().build();
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

    private java.util.List<QuestionOptionCommand> toOptionCommands(java.util.List<QuestionOptionRequest> options) {
        if (options == null) {
            return java.util.List.of();
        }
        return options.stream()
                .map(option -> new QuestionOptionCommand(
                        option.getContent(),
                        option.getIsCorrect(),
                        option.getOrderIndex()
                ))
                .toList();
    }
}
