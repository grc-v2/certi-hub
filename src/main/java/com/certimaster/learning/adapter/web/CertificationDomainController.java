package com.certimaster.learning.adapter.web;

import com.certimaster.learning.application.command.CreateCertificationDomainCommand;
import com.certimaster.learning.application.command.DeleteCertificationDomainCommand;
import com.certimaster.learning.application.command.UpdateCertificationDomainCommand;
import com.certimaster.learning.adapter.web.request.CertificationDomainRequest;
import com.certimaster.learning.adapter.web.response.CertificationDomainResponse;
import com.certimaster.learning.application.command.CertificationDomainCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CertificationDomainController {

    private final CertificationDomainCommandService certificationDomainCommandService;

    public CertificationDomainController(CertificationDomainCommandService certificationDomainCommandService) {
        this.certificationDomainCommandService = certificationDomainCommandService;
    }

    @PostMapping("/admin/certifications/{certificationId}/domains")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificationDomainResponse> createDomain(
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationDomainRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificationDomainCommandService.create(toCreateCommand(certificationId, request)));
    }

    @PutMapping("/admin/domains/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificationDomainResponse> updateDomain(
            @PathVariable Long id,
            @Valid @RequestBody CertificationDomainRequest request) {
        return ResponseEntity.ok(certificationDomainCommandService.update(toUpdateCommand(id, request)));
    }

    @DeleteMapping("/admin/domains/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDomain(@PathVariable Long id) {
        certificationDomainCommandService.delete(new DeleteCertificationDomainCommand(id));
        return ResponseEntity.noContent().build();
    }

    private CreateCertificationDomainCommand toCreateCommand(Long certificationId, CertificationDomainRequest request) {
        return new CreateCertificationDomainCommand(
                certificationId,
                request.getName(),
                request.getDescription(),
                request.getDisplayOrder(),
                request.getParentDomainId()
        );
    }

    private UpdateCertificationDomainCommand toUpdateCommand(Long id, CertificationDomainRequest request) {
        return new UpdateCertificationDomainCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getDisplayOrder(),
                request.getParentDomainId()
        );
    }
}
