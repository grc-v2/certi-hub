package com.certimaster.learning.application.command;

public record CompleteCourseLessonCommand(Long courseId, Long lessonId, Long userId) {
}
