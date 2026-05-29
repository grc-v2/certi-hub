package com.certimaster.shared.dto;

/**
 * Structured envelope for all API responses.
 *
 * @param <T> type of the business payload
 */
public class ApiResponse<T> {

    private ApiResponseMetadata metadata;
    private T data;

    public ApiResponse() {
    }

    private ApiResponse(ApiResponseMetadata metadata, T data) {
        this.metadata = metadata;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(ApiResponseMetadata metadata, T data) {
        return new ApiResponse<>(metadata, data);
    }

    public ApiResponseMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ApiResponseMetadata metadata) {
        this.metadata = metadata;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
