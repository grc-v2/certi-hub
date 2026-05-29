package com.certimaster.learning.application.command;

public record UpdateCertificationDomainCommand(
        Long id,
        String name,
        String description,
        Integer displayOrder,
        Long parentDomainId
) {
}
