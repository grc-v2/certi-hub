package com.certimaster.engagement.adapter.web.response;

public class ReactionResponse {

    private Long id;
    private Long userId;
    private String reactionType;

    public ReactionResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getReactionType() { return reactionType; }
    public void setReactionType(String reactionType) { this.reactionType = reactionType; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long userId;
        private String reactionType;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder reactionType(String v) { this.reactionType = v; return this; }

        public ReactionResponse build() {
            ReactionResponse o = new ReactionResponse();
            o.id = this.id;
            o.userId = this.userId;
            o.reactionType = this.reactionType;
            return o;
        }
    }
}
