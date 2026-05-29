package com.certimaster.identity.adapter.web.response;

import com.certimaster.identity.domain.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private Long actorId;
    private String actorUsername;
    private String action;
    private String resourceType;
    private String resourceId;
    private String httpMethod;
    private String httpPath;
    private String queryString;
    private String ipAddress;
    private String userAgent;
    private Integer statusCode;
    private Long durationMs;
    private String serviceName;
    private LocalDateTime occurredAt;

    public static AuditLogResponse from(AuditLog entry) {
        AuditLogResponse r = new AuditLogResponse();
        r.id = entry.getId();
        r.actorId = entry.getActorId();
        r.actorUsername = entry.getActorUsername();
        r.action = entry.getAction();
        r.resourceType = entry.getResourceType();
        r.resourceId = entry.getResourceId();
        r.httpMethod = entry.getHttpMethod();
        r.httpPath = entry.getHttpPath();
        r.queryString = entry.getQueryString();
        r.ipAddress = entry.getIpAddress();
        r.userAgent = entry.getUserAgent();
        r.statusCode = entry.getStatusCode();
        r.durationMs = entry.getDurationMs();
        r.serviceName = entry.getServiceName();
        r.occurredAt = entry.getOccurredAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getActorId() { return actorId; }
    public String getActorUsername() { return actorUsername; }
    public String getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getHttpMethod() { return httpMethod; }
    public String getHttpPath() { return httpPath; }
    public String getQueryString() { return queryString; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Integer getStatusCode() { return statusCode; }
    public Long getDurationMs() { return durationMs; }
    public String getServiceName() { return serviceName; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
