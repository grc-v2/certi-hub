package com.certimaster.identity.adapter.persistence;

import com.certimaster.identity.domain.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
            SELECT a FROM AuditLog a
            WHERE (:actorId IS NULL OR a.actorId = :actorId)
              AND (:resourceType IS NULL OR a.resourceType = :resourceType)
              AND (:from IS NULL OR a.occurredAt >= :from)
              AND (:to IS NULL OR a.occurredAt <= :to)
            """)
    Page<AuditLog> search(
            @Param("actorId") Long actorId,
            @Param("resourceType") String resourceType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
