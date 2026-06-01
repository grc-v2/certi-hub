package com.certimaster.identity.application.query;

import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.identity.adapter.web.response.AuditLogResponse;
import com.certimaster.identity.adapter.persistence.AuditLogRepository;
import com.certimaster.identity.domain.entity.AuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return repository.findAll(toSpecification(actorId, resourceType, from, to), pageable)
                .map(AuditLogResponse::from);
    }

    public AuditLogResponse getById(Long id) {
        return repository.findById(id)
                .map(AuditLogResponse::from)
                .orElseThrow(() -> ResourceNotFoundException.byId("AuditLog", id));
    }

    private Specification<AuditLog> toSpecification(Long actorId,
                                                    String resourceType,
                                                    LocalDateTime from,
                                                    LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (actorId != null) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            if (resourceType != null && !resourceType.isBlank()) {
                predicates.add(cb.equal(root.get("resourceType"), resourceType));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("occurredAt"), to));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
