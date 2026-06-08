package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.adapter.web.response.CourseEnrollmentResponse;
import com.certimaster.learning.adapter.web.response.CourseLessonDetailResponse;
import com.certimaster.learning.adapter.web.response.CourseLessonSummaryResponse;
import com.certimaster.learning.adapter.web.response.CourseModuleResponse;
import com.certimaster.learning.adapter.web.response.CourseResponse;
import com.certimaster.learning.domain.entity.Course;
import com.certimaster.learning.domain.entity.CourseEnrollment;
import com.certimaster.learning.domain.entity.CourseLesson;
import com.certimaster.learning.domain.entity.CourseModule;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
public class CourseMapper {

    public CourseResponse toSummaryResponse(Course course, int totalLessons) {
        return baseCourseResponse(course)
                .totalModules(course.getModules() != null ? course.getModules().size() : 0)
                .totalLessons(totalLessons)
                .modules(List.of())
                .build();
    }

    public CourseResponse toDetailResponse(Course course, Set<Long> completedLessonIds) {
        List<CourseModuleResponse> modules = course.getModules() == null
                ? List.of()
                : course.getModules().stream()
                .sorted(Comparator.comparing(CourseModule::getOrderIndex, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(CourseModule::getId, Comparator.nullsLast(Long::compareTo)))
                .map(module -> toModuleResponse(module, completedLessonIds))
                .toList();

        int totalLessons = modules.stream()
                .mapToInt(module -> module.getLessonsCount() != null ? module.getLessonsCount() : 0)
                .sum();

        return baseCourseResponse(course)
                .totalModules(modules.size())
                .totalLessons(totalLessons)
                .modules(modules)
                .build();
    }

    public CourseEnrollmentResponse toEnrollmentResponse(CourseEnrollment enrollment, int totalLessons) {
        return CourseEnrollmentResponse.builder()
                .id(enrollment.getId())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .createdBy(enrollment.getCreatedBy())
                .updatedBy(enrollment.getUpdatedBy())
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .userId(enrollment.getUserId())
                .status(enrollment.getStatus())
                .progressPercentage(enrollment.getProgressPercentage())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .enrolledAt(enrollment.getEnrolledAt())
                .course(enrollment.getCourse() != null ? toSummaryResponse(enrollment.getCourse(), totalLessons) : null)
                .build();
    }

    public CourseLessonDetailResponse toLessonDetailResponse(CourseLesson lesson,
                                                             Long courseId,
                                                             boolean completed,
                                                             Long nextLessonId,
                                                             Long prevLessonId) {
        CourseModule module = lesson.getModule();

        return CourseLessonDetailResponse.builder()
                .id(lesson.getId())
                .courseId(courseId)
                .moduleId(module != null ? module.getId() : null)
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .orderIndex(lesson.getOrderIndex())
                .durationMinutes(lesson.getDurationMinutes())
                .type(lesson.getLessonType())
                .isCompleted(completed)
                .nextLessonId(nextLessonId)
                .prevLessonId(prevLessonId)
                .build();
    }

    private CourseResponse.Builder baseCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .createdBy(course.getCreatedBy())
                .updatedBy(course.getUpdatedBy())
                .certificationId(course.getCertification() != null ? course.getCertification().getId() : null)
                .certificationName(course.getCertification() != null ? course.getCertification().getName() : null)
                .name(course.getName())
                .description(course.getDescription())
                .instructor(course.getInstructor())
                .thumbnailUrl(course.getThumbnailUrl())
                .durationMinutes(course.getDurationMinutes())
                .enrolledCount(course.getEnrolledCount())
                .published(course.getPublished());
    }

    private CourseModuleResponse toModuleResponse(CourseModule module, Set<Long> completedLessonIds) {
        List<CourseLessonSummaryResponse> lessons = module.getLessons() == null
                ? List.of()
                : module.getLessons().stream()
                .sorted(Comparator.comparing(CourseLesson::getOrderIndex, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(CourseLesson::getId, Comparator.nullsLast(Long::compareTo)))
                .map(lesson -> toLessonSummaryResponse(lesson, completedLessonIds.contains(lesson.getId())))
                .toList();

        return CourseModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .lessonsCount(lessons.size())
                .lessons(lessons)
                .build();
    }

    private CourseLessonSummaryResponse toLessonSummaryResponse(CourseLesson lesson, boolean completed) {
        return CourseLessonSummaryResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .durationMinutes(lesson.getDurationMinutes())
                .type(lesson.getLessonType())
                .isCompleted(completed)
                .build();
    }
}
