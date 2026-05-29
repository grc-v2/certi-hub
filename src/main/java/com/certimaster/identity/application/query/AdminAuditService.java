package com.certimaster.identity.application.query;

import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.identity.adapter.web.response.AuditLogResponse;
import com.certimaster.identity.adapter.persistence.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminAuditService {

    private final AuditLogRepository repository;

    public AdminAuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public Page<AuditLogResponse> search(
            Long actorId,
            String resourceType,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        return repository.search(actorId, resourceType, from, to, pageable)
                .map(AuditLogResponse::from);
    }

    public AuditLogResponse getById(Long id) {
        return repository.findById(id)
                .map(AuditLogResponse::from)
                .orElseThrow(() -> ResourceNotFoundException.byId("AuditLog", id));
    }
}
