package com.certimaster.productivity.adapter.web.request;

/**
 * Payload to add a resource. Provide {@code url} for a link (kind auto-detected,
 * title auto-fetched if omitted) or {@code content} for a text note. {@code kind}
 * is an optional override for links.
 */
public class CreateResourceRequest {

    private String url;
    private String title;
    private String content;
    private String kind;
    private Long collectionId;

    public CreateResourceRequest() {
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public Long getCollectionId() { return collectionId; }
    public void setCollectionId(Long collectionId) { this.collectionId = collectionId; }
}
