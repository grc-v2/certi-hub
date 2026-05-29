package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagWriteRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.id = :id AND (t.status IS NULL OR t.status != 'DELETED')")
    Optional<Tag> findActiveById(@Param("id") Long id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT t FROM Tag t WHERE t.id IN :ids AND (t.status IS NULL OR t.status != 'DELETED')")
    List<Tag> findByIdIn(@Param("ids") List<Long> ids);
}
