package com.certimaster.learning.adapter.web.request;

/**
 * Reserved for forward-compatible options when starting an EXAM session.
 * Server forces mode = EXAM; any client-supplied mode is ignored.
 */
public class StartExamRequest {

    public StartExamRequest() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public StartExamRequest build() {
            return new StartExamRequest();
        }
    }
}
