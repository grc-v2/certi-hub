package com.certimaster.engagement.adapter.persistence;

import com.certimaster.engagement.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostWriteRepository extends JpaRepository<Post, Long> {

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query("SELECT p FROM Post p WHERE p.status = 'SCHEDULED' AND p.scheduledPublishAt <= :now")
    List<Post> findScheduledPostsReadyToPublish(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Post p SET p.bookmarkCount = p.bookmarkCount + :delta WHERE p.id = :id")
    void updateBookmarkCount(@Param("id") Long id, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Post p SET p.viewsCount = p.viewsCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount + 1 WHERE p.id = :id")
    void incrementCommentsCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = p.commentsCount - :count WHERE p.id = :id AND p.commentsCount >= :count")
    void decrementCommentsCount(@Param("id") Long id, @Param("count") int count);
}
