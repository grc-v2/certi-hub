package com.certimaster.learning.application.command;

import com.certimaster.learning.adapter.persistence.CourseEnrollmentRepository;
import com.certimaster.learning.adapter.persistence.CourseLessonProgressRepository;
import com.certimaster.learning.adapter.persistence.CourseLessonRepository;
import com.certimaster.learning.adapter.persistence.CourseWriteRepository;
import com.certimaster.learning.adapter.web.mapper.CourseMapper;
import com.certimaster.learning.adapter.web.response.CompleteLessonResponse;
import com.certimaster.learning.adapter.web.response.CourseEnrollmentResponse;
import com.certimaster.learning.domain.entity.Course;
import com.certimaster.learning.domain.entity.CourseEnrollment;
import com.certimaster.learning.domain.entity.CourseLesson;
import com.certimaster.learning.domain.entity.CourseLessonProgress;
import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.shared.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional
public class CourseCommandService {

    private static final Logger log = LoggerFactory.getLogger(CourseCommandService.class);
    private static final String COURSE = "Course";
    private static final String COURSE_LESSON = "Course lesson";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";

    private final CourseWriteRepository courseRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseLessonProgressRepository progressRepository;
    private final CourseMapper courseMapper;

    public CourseCommandService(CourseWriteRepository courseRepository,
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

    public CourseEnrollmentResponse enroll(EnrollCourseCommand command) {
        requireUser(command.userId());
        log.debug("Enrolling user {} in course {}", command.userId(), command.courseId());

        CourseEnrollment existing = enrollmentRepository
                .findByUserIdAndCourseIdWithCourse(command.userId(), command.courseId())
                .orElse(null);
        if (existing != null) {
            return courseMapper.toEnrollmentResponse(existing, totalLessons(command.courseId()));
        }

        Course course = courseRepository.findPublishedById(command.courseId())
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE, command.courseId()));

        LocalDateTime now = LocalDateTime.now();
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourse(course);
        enrollment.setUserId(command.userId());
        enrollment.setStatus(STATUS_IN_PROGRESS);
        enrollment.setProgressPercentage(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        enrollment.setLastAccessedAt(now);
        enrollment.setEnrolledAt(now);

        course.incrementEnrolledCount();
        courseRepository.save(course);
        CourseEnrollment saved = enrollmentRepository.save(enrollment);

        log.info("Created course enrollment {} for user {} course {}", saved.getId(), command.userId(), command.courseId());
        return courseMapper.toEnrollmentResponse(saved, totalLessons(command.courseId()));
    }

    public CompleteLessonResponse completeLesson(CompleteCourseLessonCommand command) {
        requireUser(command.userId());
        log.debug("Completing lesson {} in course {} for user {}", command.lessonId(), command.courseId(), command.userId());

        CourseEnrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseIdWithCourse(command.userId(), command.courseId())
                .orElseThrow(() -> new ForbiddenException("You must enroll in this course before completing lessons"));

        CourseLesson lesson = lessonRepository.findPublishedCourseLesson(command.courseId(), command.lessonId())
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE_LESSON, command.lessonId()));

        CourseLessonProgress progress = progressRepository.findByEnrollment_IdAndLesson_Id(enrollment.getId(), command.lessonId())
                .orElseGet(() -> {
                    CourseLessonProgress created = new CourseLessonProgress();
                    created.setEnrollment(enrollment);
                    created.setLesson(lesson);
                    return created;
                });

        if (!Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progressRepository.save(progress);
        }

        BigDecimal newProgress = recalculateProgress(enrollment);
        enrollment.setProgressPercentage(newProgress);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        if (newProgress.compareTo(BigDecimal.valueOf(100)) >= 0) {
            enrollment.setStatus(STATUS_COMPLETED);
        }
        enrollmentRepository.save(enrollment);

        return new CompleteLessonResponse("Lesson marked as completed successfully", newProgress);
    }

    private BigDecimal recalculateProgress(CourseEnrollment enrollment) {
        long total = totalLessons(enrollment.getCourse().getId());
        if (total == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long completed = progressRepository.countByEnrollment_IdAndCompletedTrue(enrollment.getId());
        return BigDecimal.valueOf(completed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    private int totalLessons(Long courseId) {
        return Math.toIntExact(lessonRepository.countByModule_Course_Id(courseId));
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw UnauthorizedException.authenticationRequired();
        }
    }
}
