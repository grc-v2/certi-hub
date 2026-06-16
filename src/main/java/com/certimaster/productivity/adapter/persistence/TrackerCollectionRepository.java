package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.TrackerCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackerCollectionRepository extends JpaRepository<TrackerCollection, Long> {

    List<TrackerCollection> findByUserIdOrderByNameAsc(Long userId);

    Optional<TrackerCollection> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndName(Long userId, String name);

    void deleteByUserId(Long userId);
}
