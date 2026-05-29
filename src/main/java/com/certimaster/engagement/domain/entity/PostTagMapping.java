package com.certimaster.engagement.domain.entity;

import com.certimaster.engagement.domain.entity.PostTag;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_tag_mappings")
public class PostTagMapping {

    @EmbeddedId
    private PostTagMappingId id = new PostTagMappingId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private PostTag tag;

    public PostTagMapping() {
    }

    public PostTagMapping(PostTagMappingId id, Post post, PostTag tag) {
        this.id = id;
        this.post = post;
        this.tag = tag;
    }

    public PostTagMapping(Post post, PostTag tag) {
        this.post = post;
        this.tag = tag;
        this.id = new PostTagMappingId(post.getId(), tag.getId());
    }

    public PostTagMappingId getId() { return id; }
    public void setId(PostTagMappingId id) { this.id = id; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public PostTag getTag() { return tag; }
    public void setTag(PostTag tag) { this.tag = tag; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private PostTagMappingId id = new PostTagMappingId();
        private Post post;
        private PostTag tag;

        public Builder id(PostTagMappingId v) { this.id = v; return this; }
        public Builder post(Post v) { this.post = v; return this; }
        public Builder tag(PostTag v) { this.tag = v; return this; }

        public PostTagMapping build() {
            PostTagMapping o = new PostTagMapping();
            o.id = this.id;
            o.post = this.post;
            o.tag = this.tag;
            return o;
        }
    }
}
