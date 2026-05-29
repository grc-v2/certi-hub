package com.certimaster.identity.application.command;

import com.certimaster.identity.domain.entity.AuditLog;
import com.certimaster.identity.adapter.persistence.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Writes an audit entry off the request thread so latency of admin endpoints
     * is not affected by DB pressure.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AuditLog entry) {
        try {
            repository.save(entry);
        } catch (Exception ex) {
            log.warn("Failed to persist audit log action={} resource={}/{}: {}",
                    entry.getAction(), entry.getResourceType(), entry.getResourceId(), ex.getMessage());
        }
    }
}
