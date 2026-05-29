package com.certimaster.engagement.adapter.persistence;

import com.certimaster.engagement.domain.entity.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostTagReadRepository extends JpaRepository<PostTag, Long> {

    Optional<PostTag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT t, COUNT(DISTINCT ptm.post.id) as postCount
            FROM PostTag t
            LEFT JOIN t.postMappings ptm
            LEFT JOIN ptm.post p ON p.status = 'PUBLISHED'
            GROUP BY t
            ORDER BY t.name
            """)
    List<Object[]> findAllWithPostCount();

    @Query("""
            SELECT COUNT(DISTINCT ptm.post.id)
            FROM PostTagMapping ptm
            WHERE ptm.tag.id = :tagId
            AND ptm.post.status = 'PUBLISHED'
            """)
    long countPublishedPostsByTagId(@Param("tagId") Long tagId);

    @Query("""
            SELECT t
            FROM PostTag t
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.slug) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<PostTag> search(@Param("keyword") String keyword, Pageable pageable);
}
