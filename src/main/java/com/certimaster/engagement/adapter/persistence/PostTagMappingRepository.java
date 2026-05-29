package com.certimaster.engagement.adapter.persistence;

import com.certimaster.engagement.domain.entity.PostTagMapping;
import com.certimaster.engagement.domain.entity.PostTagMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagMappingRepository extends JpaRepository<PostTagMapping, PostTagMappingId> {

    @Modifying
    @Query("DELETE FROM PostTagMapping ptm WHERE ptm.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostTagMapping ptm WHERE ptm.tag.id = :tagId")
    void deleteByTagId(@Param("tagId") Long tagId);

}
