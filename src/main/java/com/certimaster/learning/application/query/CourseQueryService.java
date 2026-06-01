package com.certimaster.learning.application.query;

import com.certimaster.learning.adapter.persistence.CourseEnrollmentRepository;
import com.certimaster.learning.adapter.persistence.CourseLessonProgressRepository;
import com.certimaster.learning.adapter.persistence.CourseLessonRepository;
import com.certimaster.learning.adapter.persistence.CourseReadRepository;
import com.certimaster.learning.adapter.web.mapper.CourseMapper;
import com.certimaster.learning.adapter.web.response.CourseEnrollmentResponse;
import com.certimaster.learning.adapter.web.response.CourseLessonDetailResponse;
import com.certimaster.learning.adapter.web.response.CourseResponse;
import com.certimaster.learning.domain.entity.Course;
import com.certimaster.learning.domain.entity.CourseEnrollment;
import com.certimaster.learning.domain.entity.CourseLesson;
import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.shared.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CourseQueryService {

    private static final Logger log = LoggerFactory.getLogger(CourseQueryService.class);
    private static final String COURSE = "Course";
    private static final String COURSE_LESSON = "Course lesson";

    private final CourseReadRepository courseRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseLessonProgressRepository progressRepository;
    private final CourseMapper courseMapper;

    public CourseQueryService(CourseReadRepository courseRepository,
                              CourseLessonRepository lessonRepository,
                              CourseEnrollmentRepository enrollmentRepository,
                              CourseLessonProgressRepository progressRepository,
                              CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
        this.courseMapper = courseMapper;
    }

    public PageResponse<CourseResponse> search(SearchCoursesQuery query) {
        log.debug("Searching published courses with criteria: {}", query);

        Pageable pageable = PageRequest.of(query.page(), query.size(), createSort(query.sortBy(), query.sortDirection()));
        Page<Course> page = courseRepository.searchPublished(query.certificationId(), query.search(), pageable);

        return PageResponse.of(page.map(course -> courseMapper.toSummaryResponse(
                course,
                Math.toIntExact(lessonRepository.countByModule_Course_Id(course.getId()))
        )));
    }

    public CourseResponse getById(GetCourseByIdQuery query) {
        log.debug("Getting course {} for user {}", query.courseId(), query.userId());

        Course course = courseRepository.findPublishedDetailById(query.courseId())
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE, query.courseId()));

        Set<Long> completedLessonIds = query.userId() != null
                ? new HashSet<>(progressRepository.findCompletedLessonIds(query.userId(), query.courseId()))
                : Set.of();

        return courseMapper.toDetailResponse(course, completedLessonIds);
    }

    public List<CourseEnrollmentResponse> getMyEnrollments(GetMyCourseEnrollmentsQuery query) {
        requireUser(query.userId());
        log.debug("Getting course enrollments for user {}", query.userId());

        return enrollmentRepository.findByUserIdWithCourse(query.userId()).stream()
                .map(enrollment -> courseMapper.toEnrollmentResponse(
                        enrollment,
                        Math.toIntExact(lessonRepository.countByModule_Course_Id(enrollment.getCourse().getId()))
                ))
                .toList();
    }

    public CourseLessonDetailResponse getLesson(GetCourseLessonQuery query) {
        requireUser(query.userId());
        log.debug("Getting course lesson {} in course {} for user {}", query.lessonId(), query.courseId(), query.userId());

        CourseEnrollment enrollment = enrollmentRepository.findByUserIdAndCourseIdWithCourse(query.userId(), query.courseId())
                .orElseThrow(() -> new ForbiddenException("You must enroll in this course before accessing lessons"));

        CourseLesson lesson = lessonRepository.findPublishedCourseLesson(query.courseId(), query.lessonId())
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE_LESSON, query.lessonId()));

        boolean completed = progressRepository.findByEnrollment_IdAndLesson_Id(enrollment.getId(), query.lessonId())
                .map(progress -> Boolean.TRUE.equals(progress.getCompleted()))
                .orElse(false);

        List<Long> orderedLessonIds = lessonRepository.findOrderedLessonIdsByCourseId(query.courseId());
        int currentIndex = orderedLessonIds.indexOf(query.lessonId());
        Long prevLessonId = currentIndex > 0 ? orderedLessonIds.get(currentIndex - 1) : null;
        Long nextLessonId = currentIndex >= 0 && currentIndex < orderedLessonIds.size() - 1
                ? orderedLessonIds.get(currentIndex + 1)
                : null;

        return courseMapper.toLessonDetailResponse(lesson, query.courseId(), completed, nextLessonId, prevLessonId);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        String resolvedSort = switch (sortBy == null ? "" : sortBy) {
            case "name", "durationMinutes", "enrolledCount", "createdAt", "updatedAt" -> sortBy;
            default -> "createdAt";
        };
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, resolvedSort);
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw UnauthorizedException.authenticationRequired();
        }
    }
}
