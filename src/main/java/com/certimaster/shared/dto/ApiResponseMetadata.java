package com.certimaster.shared.dto;

/**
 * Technical envelope metadata for every API response.
 */
public class ApiResponseMetadata {

    private String id;
    private String version;
    private String timestamp;
    private String domain;
    private String service;
    private String type;
    private String correlationId;
    private int statusCode;
    private boolean success;

    public ApiResponseMetadata() {
    }

    private ApiResponseMetadata(Builder b) {
        this.id = b.id;
        this.version = b.version;
        this.timestamp = b.timestamp;
        this.domain = b.domain;
        this.service = b.service;
        this.type = b.type;
        this.correlationId = b.correlationId;
        this.statusCode = b.statusCode;
        this.success = b.success;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String version;
        private String timestamp;
        private String domain;
        private String service;
        private String type;
        private String correlationId;
        private int statusCode;
        private boolean success;

        public Builder id(String v) { this.id = v; return this; }
        public Builder version(String v) { this.version = v; return this; }
        public Builder timestamp(String v) { this.timestamp = v; return this; }
        public Builder domain(String v) { this.domain = v; return this; }
        public Builder service(String v) { this.service = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder correlationId(String v) { this.correlationId = v; return this; }
        public Builder statusCode(int v) { this.statusCode = v; return this; }
        public Builder success(boolean v) { this.success = v; return this; }

        public ApiResponseMetadata build() {
            return new ApiResponseMetadata(this);
        }
    }

    public String getId() { return id; }
    public String getVersion() { return version; }
    public String getTimestamp() { return timestamp; }
    public String getDomain() { return domain; }
    public String getService() { return service; }
    public String getType() { return type; }
    public String getCorrelationId() { return correlationId; }
    public int getStatusCode() { return statusCode; }
    public boolean isSuccess() { return success; }
}
