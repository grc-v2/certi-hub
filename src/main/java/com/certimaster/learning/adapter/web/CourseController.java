package com.certimaster.learning.adapter.web;

import com.certimaster.learning.adapter.web.response.CompleteLessonResponse;
import com.certimaster.learning.adapter.web.response.CourseEnrollmentResponse;
import com.certimaster.learning.adapter.web.response.CourseLessonDetailResponse;
import com.certimaster.learning.adapter.web.response.CourseResponse;
import com.certimaster.learning.application.command.CompleteCourseLessonCommand;
import com.certimaster.learning.application.command.CourseCommandService;
import com.certimaster.learning.application.command.EnrollCourseCommand;
import com.certimaster.learning.application.query.CourseQueryService;
import com.certimaster.learning.application.query.GetCourseByIdQuery;
import com.certimaster.learning.application.query.GetCourseLessonQuery;
import com.certimaster.learning.application.query.GetMyCourseEnrollmentsQuery;
import com.certimaster.learning.application.query.SearchCoursesQuery;
import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseQueryService queryService;
    private final CourseCommandService commandService;

    public CourseController(CourseQueryService queryService,
                            CourseCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping("/courses")
    public ResponseEntity<PageResponse<CourseResponse>> searchCourses(
            @RequestParam(required = false) Long certificationId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        int resolvedSize = limit != null ? limit : (size != null ? size : 10);
        SearchCoursesQuery query = new SearchCoursesQuery(
                certificationId,
                search,
                page,
                resolvedSize,
                sortBy,
                sortDirection
        );
        return ResponseEntity.ok(queryService.search(query));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getById(new GetCourseByIdQuery(id, SecurityUtils.getCurrentUserId())));
    }

    @PostMapping("/courses/{id}/enroll")
    public ResponseEntity<CourseEnrollmentResponse> enrollCourse(@PathVariable Long id) {
        CourseEnrollmentResponse enrollment = commandService.enroll(new EnrollCourseCommand(
                id,
                SecurityUtils.getCurrentUserId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @GetMapping("/users/me/enrollments")
    public ResponseEntity<List<CourseEnrollmentResponse>> getMyEnrollments() {
        return ResponseEntity.ok(queryService.getMyEnrollments(new GetMyCourseEnrollmentsQuery(
                SecurityUtils.getCurrentUserId()
        )));
    }

    @GetMapping("/courses/{id}/lessons/{lessonId}")
    public ResponseEntity<CourseLessonDetailResponse> getLesson(@PathVariable Long id,
                                                                @PathVariable Long lessonId) {
        return ResponseEntity.ok(queryService.getLesson(new GetCourseLessonQuery(
                id,
                lessonId,
                SecurityUtils.getCurrentUserId()
        )));
    }

    @PostMapping("/courses/{id}/lessons/{lessonId}/complete")
    public ResponseEntity<CompleteLessonResponse> completeLesson(@PathVariable Long id,
                                                                 @PathVariable Long lessonId) {
        return ResponseEntity.ok(commandService.completeLesson(new CompleteCourseLessonCommand(
                id,
                lessonId,
                SecurityUtils.getCurrentUserId()
        )));
    }
}
