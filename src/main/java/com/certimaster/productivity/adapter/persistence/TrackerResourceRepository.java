package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.TrackerResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackerResourceRepository extends JpaRepository<TrackerResource, Long> {

    List<TrackerResource> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<TrackerResource> findByIdAndUserId(Long id, Long userId);

    /** Detach all of a user's resources from a collection (used when it's deleted). */
    @Modifying
    @Query("update TrackerResource r set r.collectionId = null where r.userId = :userId and r.collectionId = :collectionId")
    void clearCollection(@Param("userId") Long userId, @Param("collectionId") Long collectionId);

    void deleteByUserId(Long userId);
}
