package com.certimaster.learning.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.learning.application.command.CreateExamCommand;
import com.certimaster.learning.application.command.DeleteExamCommand;
import com.certimaster.learning.application.command.UpdateExamCommand;
import com.certimaster.learning.application.query.GetExamByIdQuery;
import com.certimaster.learning.application.query.GetExamsByCertificationQuery;
import com.certimaster.learning.application.query.SearchExamsQuery;
import com.certimaster.learning.adapter.web.request.ExamRequest;
import com.certimaster.learning.adapter.web.response.ExamDetailResponse;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.application.command.ExamCommandService;
import com.certimaster.learning.application.query.ExamQueryService;
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
@RequestMapping("/api/v1/exams")
public class ExamController {

    private final ExamCommandService examCommandService;
    private final ExamQueryService examQueryService;

    public ExamController(ExamCommandService examCommandService,
                          ExamQueryService examQueryService) {
        this.examCommandService = examCommandService;
        this.examQueryService = examQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ExamResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long certificationId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        SearchExamsQuery query = new SearchExamsQuery(
                keyword,
                certificationId,
                type,
                status,
                page,
                size,
                sortBy,
                sortDirection
        );
        return ResponseEntity.ok(examQueryService.search(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(examQueryService.getById(new GetExamByIdQuery(id)));
    }

    @GetMapping("/certification/{certificationId}")
    public ResponseEntity<List<ExamResponse>> getByCertificationId(@PathVariable Long certificationId) {
        return ResponseEntity.ok(examQueryService.getByCertificationId(new GetExamsByCertificationQuery(certificationId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<ExamResponse> create(@Valid @RequestBody ExamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examCommandService.create(toCreateCommand(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<ExamResponse> update(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(examCommandService.update(toUpdateCommand(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examCommandService.delete(new DeleteExamCommand(id));
        return ResponseEntity.noContent().build();
    }

    private CreateExamCommand toCreateCommand(ExamRequest request) {
        return new CreateExamCommand(
                request.getCertificationId(),
                request.getTitle(),
                request.getType(),
                request.getDescription(),
                request.getDurationMinutes(),
                request.getTotalQuestions(),
                request.getPassingScore(),
                request.getStatus()
        );
    }

    private UpdateExamCommand toUpdateCommand(Long id, ExamRequest request) {
        return new UpdateExamCommand(
                id,
                request.getCertificationId(),
                request.getTitle(),
                request.getType(),
                request.getDescription(),
                request.getDurationMinutes(),
                request.getTotalQuestions(),
                request.getPassingScore(),
                request.getStatus()
        );
    }
}
