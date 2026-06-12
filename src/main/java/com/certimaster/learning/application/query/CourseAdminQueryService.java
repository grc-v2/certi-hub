package com.certimaster.learning.application.query;

import com.certimaster.learning.adapter.persistence.CourseLessonRepository;
import com.certimaster.learning.adapter.persistence.CourseReadRepository;
import com.certimaster.learning.adapter.web.mapper.CourseMapper;
import com.certimaster.learning.adapter.web.response.CourseLessonDetailResponse;
import com.certimaster.learning.adapter.web.response.CourseResponse;
import com.certimaster.learning.domain.entity.Course;
import com.certimaster.learning.domain.entity.CourseLesson;
import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CourseAdminQueryService {

    private static final String COURSE = "Course";
    private static final String COURSE_LESSON = "Course lesson";

    private final CourseReadRepository courseRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseMapper courseMapper;

    public CourseAdminQueryService(CourseReadRepository courseRepository,
                                   CourseLessonRepository lessonRepository,
                                   CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.courseMapper = courseMapper;
    }

    public PageResponse<CourseResponse> search(SearchAdminCoursesQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size(),
                createSort(query.sortBy(), query.sortDirection()));
        Page<Course> page = courseRepository.searchAdmin(
                query.certificationId(),
                query.search(),
                query.published(),
                pageable
        );

        return PageResponse.of(page.map(course -> courseMapper.toSummaryResponse(
                course,
                Math.toIntExact(lessonRepository.countByModule_Course_Id(course.getId()))
        )));
    }

    public CourseResponse getDetail(Long courseId) {
        Course course = courseRepository.findDetailById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE, courseId));
        return courseMapper.toDetailResponse(course, Set.of());
    }

    public CourseLessonDetailResponse getLessonDetail(Long courseId, Long moduleId, Long lessonId) {
        CourseLesson lesson = lessonRepository.findByCourseModuleAndLessonId(courseId, moduleId, lessonId)
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE_LESSON, lessonId));
        return courseMapper.toLessonDetailResponse(lesson, courseId, false, null, null);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        String resolvedSort = switch (sortBy == null ? "" : sortBy) {
            case "name", "durationMinutes", "enrolledCount", "createdAt", "updatedAt" -> sortBy;
            default -> "createdAt";
        };
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, resolvedSort);
    }
}
