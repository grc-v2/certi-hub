package com.certimaster.learning.application.command;

import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import com.certimaster.learning.adapter.persistence.CourseLessonRepository;
import com.certimaster.learning.adapter.persistence.CourseModuleRepository;
import com.certimaster.learning.adapter.persistence.CourseReadRepository;
import com.certimaster.learning.adapter.persistence.CourseWriteRepository;
import com.certimaster.learning.adapter.web.mapper.CourseMapper;
import com.certimaster.learning.adapter.web.request.CourseLessonRequest;
import com.certimaster.learning.adapter.web.request.CourseModuleRequest;
import com.certimaster.learning.adapter.web.request.CourseRequest;
import com.certimaster.learning.adapter.web.response.CourseResponse;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.domain.entity.Course;
import com.certimaster.learning.domain.entity.CourseLesson;
import com.certimaster.learning.domain.entity.CourseModule;
import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class CourseAdminCommandService {

    private static final String COURSE = "Course";
    private static final String COURSE_MODULE = "Course module";
    private static final String COURSE_LESSON = "Course lesson";
    private static final String CERTIFICATION = "Certification";

    private final CourseWriteRepository courseWriteRepository;
    private final CourseReadRepository courseReadRepository;
    private final CourseModuleRepository moduleRepository;
    private final CourseLessonRepository lessonRepository;
    private final CertificationWriteRepository certificationRepository;
    private final CourseMapper courseMapper;

    public CourseAdminCommandService(CourseWriteRepository courseWriteRepository,
                                     CourseReadRepository courseReadRepository,
                                     CourseModuleRepository moduleRepository,
                                     CourseLessonRepository lessonRepository,
                                     CertificationWriteRepository certificationRepository,
                                     CourseMapper courseMapper) {
        this.courseWriteRepository = courseWriteRepository;
        this.courseReadRepository = courseReadRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.certificationRepository = certificationRepository;
        this.courseMapper = courseMapper;
    }

    public CourseResponse create(CourseRequest request) {
        Certification certification = findCertificationOrThrow(request.getCertificationId());

        Course course = new Course();
        course.setCertification(certification);
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setInstructor(request.getInstructor());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setDurationMinutes(request.getDurationMinutes());
        course.setEnrolledCount(0);
        course.setPublished(false);

        Course saved = courseWriteRepository.save(course);
        return courseMapper.toDetailResponse(saved, Set.of());
    }

    public CourseResponse update(Long courseId, CourseRequest request) {
        Course course = findCourseOrThrow(courseId);
        if (request.getCertificationId() != null
                && (course.getCertification() == null
                || !request.getCertificationId().equals(course.getCertification().getId()))) {
            course.setCertification(findCertificationOrThrow(request.getCertificationId()));
        }
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setInstructor(request.getInstructor());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setDurationMinutes(request.getDurationMinutes());

        Course saved = courseWriteRepository.save(course);
        return reloadDetail(saved.getId());
    }

    public void delete(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        courseWriteRepository.delete(course);
    }

    public CourseResponse publish(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        if (course.getModules() == null || course.getModules().isEmpty()) {
            throw BusinessException.invalidInput("Course must contain at least one module before publishing");
        }
        long lessonCount = lessonRepository.countByModule_Course_Id(courseId);
        if (lessonCount == 0) {
            throw BusinessException.invalidInput("Course must contain at least one lesson before publishing");
        }
        course.setPublished(true);
        courseWriteRepository.save(course);
        return reloadDetail(courseId);
    }

    public CourseResponse unpublish(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        course.setPublished(false);
        courseWriteRepository.save(course);
        return reloadDetail(courseId);
    }

    public CourseResponse createModule(Long courseId, CourseModuleRequest request) {
        Course course = findCourseOrThrow(courseId);
        CourseModule module = new CourseModule();
        module.setCourse(course);
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setOrderIndex(resolveOrderIndex(request.getOrderIndex(), nextModuleOrderIndex(courseId)));
        moduleRepository.save(module);
        return reloadDetail(courseId);
    }

    public CourseResponse updateModule(Long courseId, Long moduleId, CourseModuleRequest request) {
        CourseModule module = findModuleOrThrow(courseId, moduleId);
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        if (request.getOrderIndex() != null) {
            module.setOrderIndex(request.getOrderIndex());
        }
        moduleRepository.save(module);
        return reloadDetail(courseId);
    }

    public CourseResponse deleteModule(Long courseId, Long moduleId) {
        CourseModule module = findModuleOrThrow(courseId, moduleId);
        moduleRepository.delete(module);
        return reloadDetail(courseId);
    }

    public CourseResponse createLesson(Long courseId, Long moduleId, CourseLessonRequest request) {
        CourseModule module = findModuleOrThrow(courseId, moduleId);
        CourseLesson lesson = new CourseLesson();
        lesson.setModule(module);
        applyLessonFields(lesson, request);
        lesson.setOrderIndex(resolveOrderIndex(request.getOrderIndex(), nextLessonOrderIndex(moduleId)));
        lessonRepository.save(lesson);
        return reloadDetail(courseId);
    }

    public CourseResponse updateLesson(Long courseId, Long moduleId, Long lessonId, CourseLessonRequest request) {
        CourseLesson lesson = findLessonOrThrow(courseId, moduleId, lessonId);
        applyLessonFields(lesson, request);
        if (request.getOrderIndex() != null) {
            lesson.setOrderIndex(request.getOrderIndex());
        }
        lessonRepository.save(lesson);
        return reloadDetail(courseId);
    }

    public CourseResponse deleteLesson(Long courseId, Long moduleId, Long lessonId) {
        CourseLesson lesson = findLessonOrThrow(courseId, moduleId, lessonId);
        lessonRepository.delete(lesson);
        return reloadDetail(courseId);
    }

    private void applyLessonFields(CourseLesson lesson, CourseLessonRequest request) {
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setLessonType(request.getLessonType());
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseReadRepository.findDetailById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE, courseId));
    }

    private CourseModule findModuleOrThrow(Long courseId, Long moduleId) {
        return moduleRepository.findByIdAndCourseId(courseId, moduleId)
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE_MODULE, moduleId));
    }

    private CourseLesson findLessonOrThrow(Long courseId, Long moduleId, Long lessonId) {
        return lessonRepository.findByCourseModuleAndLessonId(courseId, moduleId, lessonId)
                .orElseThrow(() -> ResourceNotFoundException.byId(COURSE_LESSON, lessonId));
    }

    private Certification findCertificationOrThrow(Long certificationId) {
        return certificationRepository.findById(certificationId)
                .orElseThrow(() -> ResourceNotFoundException.byId(CERTIFICATION, certificationId));
    }

    private Integer nextModuleOrderIndex(Long courseId) {
        Integer max = moduleRepository.findMaxOrderIndexByCourseId(courseId);
        return (max == null ? 0 : max) + 1;
    }

    private Integer nextLessonOrderIndex(Long moduleId) {
        Integer max = lessonRepository.findMaxOrderIndexByModuleId(moduleId);
        return (max == null ? 0 : max) + 1;
    }

    private Integer resolveOrderIndex(Integer requested, Integer fallback) {
        return requested != null ? requested : fallback;
    }

    private CourseResponse reloadDetail(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        return courseMapper.toDetailResponse(course, Set.of());
    }
}
