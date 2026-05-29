package com.certimaster.learning.adapter.web;

import com.certimaster.learning.application.command.CreateCertificationCommand;
import com.certimaster.learning.application.command.DeleteCertificationCommand;
import com.certimaster.learning.application.command.UpdateCertificationCommand;
import com.certimaster.learning.application.command.UpdateCertificationStatusCommand;
import com.certimaster.learning.adapter.web.request.CertificationRequest;
import com.certimaster.learning.adapter.web.response.CertificationResponse;
import com.certimaster.learning.application.command.CertificationCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/certifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCertificationController {

    private final CertificationCommandService certificationCommandService;

    public AdminCertificationController(CertificationCommandService certificationCommandService) {
        this.certificationCommandService = certificationCommandService;
    }

    @PostMapping
    public ResponseEntity<CertificationResponse> create(@Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificationCommandService.create(toCreateCommand(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificationResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody CertificationRequest request) {
        return ResponseEntity.ok(certificationCommandService.update(toUpdateCommand(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                              @RequestParam String status) {
        certificationCommandService.updateStatus(new UpdateCertificationStatusCommand(id, status));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        certificationCommandService.delete(new DeleteCertificationCommand(id));
        return ResponseEntity.noContent().build();
    }

    private CreateCertificationCommand toCreateCommand(CertificationRequest request) {
        return new CreateCertificationCommand(
                request.getName(),
                request.getCode(),
                request.getProvider(),
                request.getDescription(),
                request.getLevel(),
                request.getDurationMinutes(),
                request.getPassingScore(),
                request.getTotalQuestions(),
                request.getPrice(),
                request.getStatus()
        );
    }

    private UpdateCertificationCommand toUpdateCommand(Long id, CertificationRequest request) {
        return new UpdateCertificationCommand(
                id,
                request.getName(),
                request.getCode(),
                request.getProvider(),
                request.getDescription(),
                request.getLevel(),
                request.getDurationMinutes(),
                request.getPassingScore(),
                request.getTotalQuestions(),
                request.getPrice(),
                request.getStatus()
        );
    }
}
