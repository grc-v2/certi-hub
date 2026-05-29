package com.certimaster.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base DTO class for all DTOs in the system
 * Provides common fields for DTOs
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    protected BaseResponse() {
    }

    protected BaseResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                           String createdBy, String updatedBy) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
