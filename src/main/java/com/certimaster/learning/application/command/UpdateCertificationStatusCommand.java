package com.certimaster.learning.application.command;

public record UpdateCertificationStatusCommand(Long id, String status) {
}
