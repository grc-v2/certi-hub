package com.certimaster.engagement.adapter.persistence;

import com.certimaster.engagement.domain.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostTagWriteRepository extends JpaRepository<PostTag, Long> {

    Optional<PostTag> findByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<PostTag> findByNameIn(List<String> names);

    @Query("""
            SELECT COUNT(DISTINCT ptm.post.id)
            FROM PostTagMapping ptm
            WHERE ptm.tag.id = :tagId
            AND ptm.post.status = 'PUBLISHED'
            """)
    long countPublishedPostsByTagId(@Param("tagId") Long tagId);
}
