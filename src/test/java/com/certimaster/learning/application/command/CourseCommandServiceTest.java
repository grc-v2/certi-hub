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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseCommandServiceTest {

    @Mock
    private CourseWriteRepository courseRepository;

    @Mock
    private CourseLessonRepository lessonRepository;

    @Mock
    private CourseEnrollmentRepository enrollmentRepository;

    @Mock
    private CourseLessonProgressRepository progressRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseCommandService service;

    @Test
    void enrollCreatesEnrollmentAndIncrementsCourseCount() {
        Course course = new Course();
        course.setId(7L);
        course.setEnrolledCount(5);
        CourseEnrollmentResponse response = CourseEnrollmentResponse.builder().id(20L).courseId(7L).userId(42L).build();

        when(enrollmentRepository.findByUserIdAndCourseIdWithCourse(42L, 7L)).thenReturn(Optional.empty());
        when(courseRepository.findPublishedById(7L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonRepository.countByModule_Course_Id(7L)).thenReturn(3L);
        when(courseMapper.toEnrollmentResponse(any(CourseEnrollment.class), eq(3))).thenReturn(response);

        CourseEnrollmentResponse result = service.enroll(new EnrollCourseCommand(7L, 42L));

        ArgumentCaptor<CourseEnrollment> enrollment = ArgumentCaptor.forClass(CourseEnrollment.class);
        verify(enrollmentRepository).save(enrollment.capture());
        verify(courseRepository).save(course);
        assertThat(course.getEnrolledCount()).isEqualTo(6);
        assertThat(enrollment.getValue().getUserId()).isEqualTo(42L);
        assertThat(enrollment.getValue().getCourse()).isSameAs(course);
        assertThat(enrollment.getValue().getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(result).isSameAs(response);
    }

    @Test
    void completeLessonCreatesProgressAndRecalculatesEnrollment() {
        Course course = new Course();
        course.setId(7L);
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(9L);
        enrollment.setCourse(course);
        enrollment.setUserId(42L);
        CourseLesson lesson = new CourseLesson();
        lesson.setId(100L);

        when(enrollmentRepository.findByUserIdAndCourseIdWithCourse(42L, 7L)).thenReturn(Optional.of(enrollment));
        when(lessonRepository.findPublishedCourseLesson(7L, 100L)).thenReturn(Optional.of(lesson));
        when(progressRepository.findByEnrollment_IdAndLesson_Id(9L, 100L)).thenReturn(Optional.empty());
        when(lessonRepository.countByModule_Course_Id(7L)).thenReturn(4L);
        when(progressRepository.countByEnrollment_IdAndCompletedTrue(9L)).thenReturn(2L);

        CompleteLessonResponse result = service.completeLesson(new CompleteCourseLessonCommand(7L, 100L, 42L));

        ArgumentCaptor<CourseLessonProgress> progress = ArgumentCaptor.forClass(CourseLessonProgress.class);
        verify(progressRepository).save(progress.capture());
        verify(enrollmentRepository).save(enrollment);
        assertThat(progress.getValue().getEnrollment()).isSameAs(enrollment);
        assertThat(progress.getValue().getLesson()).isSameAs(lesson);
        assertThat(progress.getValue().getCompleted()).isTrue();
        assertThat(enrollment.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(enrollment.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(result.getNewProgressPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
    }
}
