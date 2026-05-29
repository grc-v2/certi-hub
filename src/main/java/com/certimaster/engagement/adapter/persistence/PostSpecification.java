package com.certimaster.engagement.adapter.persistence;

import com.certimaster.engagement.domain.entity.ContentType;
import com.certimaster.engagement.domain.entity.DifficultyLevel;
import com.certimaster.engagement.application.query.SearchPostsQuery;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    private PostSpecification() {}

    public static Specification<Post> fromSearchQuery(SearchPostsQuery request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.keyword())) {
                String keyword = "%" + request.keyword().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), keyword);
                Predicate contentMatch = cb.like(cb.lower(root.get("content")), keyword);
                predicates.add(cb.or(titleMatch, contentMatch));
            }

            if (StringUtils.hasText(request.status())) {
                try {
                    PostStatus status = PostStatus.valueOf(request.status().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (request.authorId() != null) {
                predicates.add(cb.equal(root.get("authorId"), request.authorId()));
            }

            if (request.categoryId() != null) {
                predicates.add(cb.equal(
                    root.join("categoryMappings").get("category").get("id"),
                    request.categoryId()
                ));
            }

            if (request.tagId() != null) {
                predicates.add(cb.equal(
                    root.join("tagMappings").get("tag").get("id"),
                    request.tagId()
                ));
            }

            if (StringUtils.hasText(request.contentType())) {
                try {
                    ContentType ct = ContentType.valueOf(request.contentType().toUpperCase());
                    predicates.add(cb.equal(root.get("contentType"), ct));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (StringUtils.hasText(request.difficultyLevel())) {
                try {
                    DifficultyLevel dl = DifficultyLevel.valueOf(request.difficultyLevel().toUpperCase());
                    predicates.add(cb.equal(root.get("difficultyLevel"), dl));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (request.seriesId() != null) {
                predicates.add(cb.equal(root.get("series").get("id"), request.seriesId()));
            }

            if (request.publishedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publishedAt"), request.publishedFrom()));
            }

            if (request.publishedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publishedAt"), request.publishedTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
