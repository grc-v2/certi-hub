package com.certimaster.learning.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.learning.application.command.AddTagsToQuestionCommand;
import com.certimaster.learning.application.command.CreateTagCommand;
import com.certimaster.learning.application.command.DeleteTagCommand;
import com.certimaster.learning.application.command.RemoveTagsFromQuestionCommand;
import com.certimaster.learning.application.command.TagCommandService;
import com.certimaster.learning.application.command.UpdateTagCommand;
import com.certimaster.learning.application.query.GetTagByIdQuery;
import com.certimaster.learning.application.query.GetTagsByQuestionQuery;
import com.certimaster.learning.application.query.SearchTagsQuery;
import com.certimaster.learning.application.query.TagQueryService;
import com.certimaster.learning.adapter.web.request.TagRequest;
import com.certimaster.learning.adapter.web.response.TagResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/question-tags")
public class TagController {

    private final TagCommandService tagCommandService;
    private final TagQueryService tagQueryService;

    public TagController(TagCommandService tagCommandService, TagQueryService tagQueryService) {
        this.tagCommandService = tagCommandService;
        this.tagQueryService = tagQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<TagResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        return ResponseEntity.ok(tagQueryService.search(new SearchTagsQuery(
                keyword,
                status,
                page,
                size,
                sortBy,
                sortDirection
        )));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TagResponse>> getAll() {
        return ResponseEntity.ok(tagQueryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tagQueryService.getById(new GetTagByIdQuery(id)));
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<TagResponse>> getByQuestionId(@PathVariable Long questionId) {
        return ResponseEntity.ok(tagQueryService.getByQuestion(new GetTagsByQuestionQuery(questionId)));
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagCommandService.create(toCreateCommand(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> update(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        return ResponseEntity.ok(tagCommandService.update(toUpdateCommand(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagCommandService.delete(new DeleteTagCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/question/{questionId}")
    public ResponseEntity<Void> addTagsToQuestion(@PathVariable Long questionId, @RequestBody List<Long> tagIds) {
        tagCommandService.addTagsToQuestion(new AddTagsToQuestionCommand(questionId, tagIds));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<Void> removeTagsFromQuestion(@PathVariable Long questionId, @RequestBody List<Long> tagIds) {
        tagCommandService.removeTagsFromQuestion(new RemoveTagsFromQuestionCommand(questionId, tagIds));
        return ResponseEntity.noContent().build();
    }

    private CreateTagCommand toCreateCommand(TagRequest request) {
        return new CreateTagCommand(request.getName(), request.getStatus());
    }

    private UpdateTagCommand toUpdateCommand(Long id, TagRequest request) {
        return new UpdateTagCommand(id, request.getName(), request.getStatus());
    }
}
