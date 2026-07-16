package com.certimaster.productivity.application.command;

/** Attach a resource to a certification, or detach it when {@code certificationId} is null. */
public record SetResourceCertificationCommand(Long resourceId, Long userId, Long certificationId) {
}
