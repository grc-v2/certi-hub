package com.certimaster.engagement.adapter.persistence;

import com.certimaster.engagement.adapter.persistence.projection.PostSummaryProjection;
import com.certimaster.engagement.adapter.persistence.projection.PostTagSummaryProjection;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReadRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    String POST_SUMMARY_SELECT = """
            p.id AS id,
            p.created_at AS "createdAt",
            p.updated_at AS "updatedAt",
            p.created_by AS "createdBy",
            p.updated_by AS "updatedBy",
            p.title AS title,
            p.slug AS slug,
            p.excerpt AS excerpt,
            p.featured_image AS "featuredImage",
            p.author_id AS "authorId",
            p.status AS status,
            p.published_at AS "publishedAt",
            p.views_count AS "viewsCount",
            p.likes_count AS "likesCount",
            p.comments_count AS "commentsCount",
            p.reading_time_minutes AS "readingTimeMinutes",
            p.content_type AS "contentType",
            p.difficulty_level AS "difficultyLevel",
            p.subtitle AS subtitle,
            p.cover_image_url AS "coverImageUrl",
            p.pinned AS pinned,
            p.featured AS featured,
            p.category AS category
            """;

    Optional<Post> findBySlug(String slug);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    @Query(value = "SELECT " + POST_SUMMARY_SELECT + """
            FROM posts p
            WHERE p.author_id = :authorId
            ORDER BY p.created_at DESC
            """,
            countQuery = "SELECT count(*) FROM posts p WHERE p.author_id = :authorId",
            nativeQuery = true)
    Page<PostSummaryProjection> findByAuthorIdSummary(
            @Param("authorId") Long authorId,
            Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            JOIN p.tagMappings tm
            WHERE tm.tag.id = :tagId
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Post> findByTagId(
            @Param("tagId") Long tagId,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Post p
            JOIN p.tagMappings tm
            WHERE tm.tag.slug = :tagSlug
            AND (:status IS NULL OR p.status = :status)
            """)
    Page<Post> findByTagSlug(
            @Param("tagSlug") String tagSlug,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT p FROM Post p
            JOIN p.tagMappings tm
            JOIN tm.tag t
            WHERE t.name IN :tagNames
            AND p.status = :status
            """)
    Page<Post> findByTagNamesAndStatus(
            @Param("tagNames") List<String> tagNames,
            @Param("status") PostStatus status,
            Pageable pageable
    );

    @Query(value = "SELECT DISTINCT " + POST_SUMMARY_SELECT + """
            FROM posts p
            JOIN post_tag_mappings tm ON tm.post_id = p.id
            JOIN post_tags t ON t.id = tm.tag_id
            WHERE t.name IN (:tagNames)
            AND p.status = CAST(:status AS text)
            ORDER BY p.published_at DESC
            """,
            countQuery = """
                    SELECT count(DISTINCT p.id)
                    FROM posts p
                    JOIN post_tag_mappings tm ON tm.post_id = p.id
                    JOIN post_tags t ON t.id = tm.tag_id
                    WHERE t.name IN (:tagNames)
                    AND p.status = CAST(:status AS text)
                    """,
            nativeQuery = true)
    Page<PostSummaryProjection> findByTagNamesAndStatusSummary(
            @Param("tagNames") List<String> tagNames,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Post p
            WHERE p.status = :status
            ORDER BY (p.viewsCount + p.likesCount * 3 + p.commentsCount * 2) DESC
            """)
    Page<Post> findTrendingPosts(@Param("status") PostStatus status, Pageable pageable);

    @Query(value = "SELECT " + POST_SUMMARY_SELECT + """
            FROM posts p
            WHERE p.status = CAST(:status AS text)
            ORDER BY (COALESCE(p.views_count, 0) + COALESCE(p.likes_count, 0) * 3 + COALESCE(p.comments_count, 0) * 2) DESC
            """,
            countQuery = "SELECT count(*) FROM posts p WHERE p.status = CAST(:status AS text)",
            nativeQuery = true)
    Page<PostSummaryProjection> findTrendingPostSummaries(
            @Param("status") String status,
            Pageable pageable);

    Page<Post> findByFeaturedTrueAndStatus(PostStatus status, Pageable pageable);

    @Query(value = "SELECT " + POST_SUMMARY_SELECT + """
            FROM posts p
            WHERE p.featured = true
            AND p.status = CAST(:status AS text)
            ORDER BY p.published_at DESC
            """,
            countQuery = """
                    SELECT count(*)
                    FROM posts p
                    WHERE p.featured = true
                    AND p.status = CAST(:status AS text)
                    """,
            nativeQuery = true)
    Page<PostSummaryProjection> findFeaturedPostSummaries(
            @Param("status") String status,
            Pageable pageable);

    @Query(value = """
            SELECT tm.post_id AS "postId",
                   t.id AS "tagId",
                   t.name AS name,
                   t.slug AS slug
            FROM post_tag_mappings tm
            JOIN post_tags t ON t.id = tm.tag_id
            WHERE tm.post_id IN (:postIds)
            ORDER BY t.display_order ASC, t.name ASC
            """,
            nativeQuery = true)
    List<PostTagSummaryProjection> findTagSummariesByPostIds(@Param("postIds") List<Long> postIds);
}
