package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

/**
 * Request DTO for retrieving user exam history with filtering and pagination.
 *
 * @see Requirements 3.1, 3.2
 */
public class UserHistoryRequest {

    /**
     * Filter by certification ID.
     */
    private Long certificationId;

    /**
     * Filter by exam ID.
     */
    private Long examId;

    /**
     * Filter by exam result status (PASSED, FAILED).
     */
    @Pattern(regexp = "^(PASSED|FAILED)$", message = "Status must be one of: PASSED, FAILED")
    private String status;

    /**
     * Filter results completed after this date.
     */
    private LocalDateTime startDate;

    /**
     * Filter results completed before this date.
     */
    private LocalDateTime endDate;

    /**
     * Page number (0-based).
     */
    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page = 0;

    /**
     * Page size.
     */
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 10;

    /**
     * Sort specification in format "field,direction" (e.g., "completedAt,desc").
     * Default: "completedAt,desc"
     */
    private String sort = "completedAt,desc";

    public UserHistoryRequest() {}

    public UserHistoryRequest(Long certificationId, Long examId, String status,
                               LocalDateTime startDate, LocalDateTime endDate,
                               Integer page, Integer size, String sort) {
        this.certificationId = certificationId;
        this.examId = examId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long certificationId;
        private Long examId;
        private String status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer page = 0;
        private Integer size = 10;
        private String sort = "completedAt,desc";

        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder startDate(LocalDateTime v) { this.startDate = v; return this; }
        public Builder endDate(LocalDateTime v) { this.endDate = v; return this; }
        public Builder page(Integer v) { this.page = v; return this; }
        public Builder size(Integer v) { this.size = v; return this; }
        public Builder sort(String v) { this.sort = v; return this; }

        public UserHistoryRequest build() {
            UserHistoryRequest o = new UserHistoryRequest();
            o.certificationId = this.certificationId;
            o.examId = this.examId;
            o.status = this.status;
            o.startDate = this.startDate;
            o.endDate = this.endDate;
            o.page = this.page;
            o.size = this.size;
            o.sort = this.sort;
            return o;
        }
    }
}
