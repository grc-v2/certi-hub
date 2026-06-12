package com.certimaster.learning.adapter.web;

import com.certimaster.learning.adapter.web.request.CourseLessonRequest;
import com.certimaster.learning.adapter.web.request.CourseModuleRequest;
import com.certimaster.learning.adapter.web.request.CourseRequest;
import com.certimaster.learning.adapter.web.response.CourseLessonDetailResponse;
import com.certimaster.learning.adapter.web.response.CourseResponse;
import com.certimaster.learning.application.command.CourseAdminCommandService;
import com.certimaster.learning.application.query.CourseAdminQueryService;
import com.certimaster.learning.application.query.SearchAdminCoursesQuery;
import com.certimaster.shared.dto.PageResponse;
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

@RestController
@RequestMapping("/api/v1/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CourseAdminQueryService queryService;
    private final CourseAdminCommandService commandService;

    public AdminCourseController(CourseAdminQueryService queryService,
                                 CourseAdminCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CourseResponse>> search(
            @RequestParam(required = false) Long certificationId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        int resolvedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int resolvedPage = Math.max(page, 0);
        return ResponseEntity.ok(queryService.search(new SearchAdminCoursesQuery(
                certificationId, search, published, resolvedPage, resolvedSize, sortBy, sortDirection
        )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getDetail(id));
    }

    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseResponse> publish(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.publish(id));
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<CourseResponse> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.unpublish(id));
    }

    @PostMapping("/{id}/modules")
    public ResponseEntity<CourseResponse> createModule(@PathVariable Long id,
                                                       @Valid @RequestBody CourseModuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.createModule(id, request));
    }

    @PutMapping("/{id}/modules/{moduleId}")
    public ResponseEntity<CourseResponse> updateModule(@PathVariable Long id,
                                                       @PathVariable Long moduleId,
                                                       @Valid @RequestBody CourseModuleRequest request) {
        return ResponseEntity.ok(commandService.updateModule(id, moduleId, request));
    }

    @DeleteMapping("/{id}/modules/{moduleId}")
    public ResponseEntity<CourseResponse> deleteModule(@PathVariable Long id,
                                                       @PathVariable Long moduleId) {
        return ResponseEntity.ok(commandService.deleteModule(id, moduleId));
    }

    @GetMapping("/{id}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<CourseLessonDetailResponse> getLessonDetail(@PathVariable Long id,
                                                                      @PathVariable Long moduleId,
                                                                      @PathVariable Long lessonId) {
        return ResponseEntity.ok(queryService.getLessonDetail(id, moduleId, lessonId));
    }

    @PostMapping("/{id}/modules/{moduleId}/lessons")
    public ResponseEntity<CourseResponse> createLesson(@PathVariable Long id,
                                                       @PathVariable Long moduleId,
                                                       @Valid @RequestBody CourseLessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandService.createLesson(id, moduleId, request));
    }

    @PutMapping("/{id}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<CourseResponse> updateLesson(@PathVariable Long id,
                                                       @PathVariable Long moduleId,
                                                       @PathVariable Long lessonId,
                                                       @Valid @RequestBody CourseLessonRequest request) {
        return ResponseEntity.ok(commandService.updateLesson(id, moduleId, lessonId, request));
    }

    @DeleteMapping("/{id}/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<CourseResponse> deleteLesson(@PathVariable Long id,
                                                       @PathVariable Long moduleId,
                                                       @PathVariable Long lessonId) {
        return ResponseEntity.ok(commandService.deleteLesson(id, moduleId, lessonId));
    }
}
