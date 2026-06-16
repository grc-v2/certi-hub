package com.certimaster.productivity.adapter.web.request;

/** Assign a resource to a collection; null/absent {@code collectionId} detaches it. */
public class SetResourceCollectionRequest {

    private Long collectionId;

    public SetResourceCollectionRequest() {
    }

    public Long getCollectionId() { return collectionId; }
    public void setCollectionId(Long collectionId) { this.collectionId = collectionId; }
}
