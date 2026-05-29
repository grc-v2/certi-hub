package com.certimaster.learning.application.command;

public record CreateCertificationDomainCommand(
        Long certificationId,
        String name,
        String description,
        Integer displayOrder,
        Long parentDomainId
) {
}
