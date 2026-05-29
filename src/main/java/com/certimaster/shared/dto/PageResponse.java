package com.certimaster.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Generic pagination wrapper for list responses
 * @param <T> Type of content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private boolean empty;

    public PageResponse() {
    }

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements,
                        int totalPages, boolean last, boolean first, boolean empty) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
        this.empty = empty;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private List<T> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
        private boolean first;
        private boolean empty;

        public Builder<T> content(List<T> v) { this.content = v; return this; }
        public Builder<T> pageNumber(int v) { this.pageNumber = v; return this; }
        public Builder<T> pageSize(int v) { this.pageSize = v; return this; }
        public Builder<T> totalElements(long v) { this.totalElements = v; return this; }
        public Builder<T> totalPages(int v) { this.totalPages = v; return this; }
        public Builder<T> last(boolean v) { this.last = v; return this; }
        public Builder<T> first(boolean v) { this.first = v; return this; }
        public Builder<T> empty(boolean v) { this.empty = v; return this; }

        public PageResponse<T> build() {
            PageResponse<T> o = new PageResponse<>();
            o.content = this.content;
            o.pageNumber = this.pageNumber;
            o.pageSize = this.pageSize;
            o.totalElements = this.totalElements;
            o.totalPages = this.totalPages;
            o.last = this.last;
            o.first = this.first;
            o.empty = this.empty;
            return o;
        }
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean empty) { this.empty = empty; }

    /**
     * Create PageDto from Spring Data Page
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * Create empty page
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .first(true)
                .empty(true)
                .build();
    }
}
