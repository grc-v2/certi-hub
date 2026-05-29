package com.certimaster.engagement.adapter.web.request;

public class BookmarkRequest {

    private Long collectionId;
    private String notes;

    public BookmarkRequest() {}

    public Long getCollectionId() { return collectionId; }
    public void setCollectionId(Long collectionId) { this.collectionId = collectionId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
