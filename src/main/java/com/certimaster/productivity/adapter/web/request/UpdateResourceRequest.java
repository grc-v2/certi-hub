package com.certimaster.productivity.adapter.web.request;

/** Partial update for a resource's editable fields (title, content, link URL). */
public class UpdateResourceRequest {

    private String title;
    private String content;
    private String url;

    public UpdateResourceRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
