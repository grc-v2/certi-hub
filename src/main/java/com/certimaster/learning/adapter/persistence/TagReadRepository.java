package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagReadRepository extends JpaRepository<Tag, Long> {

    @Query("""
            SELECT t FROM Tag t
            WHERE (t.status IS NULL OR t.status != 'DELETED')
            AND (:keyword IS NULL OR :keyword = ''
                OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:status IS NULL OR :status = '' OR t.status = :status)
            """)
    Page<Tag> search(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT t FROM Tag t WHERE t.id = :id AND (t.status IS NULL OR t.status != 'DELETED')")
    Optional<Tag> findActiveById(@Param("id") Long id);

    @Query("SELECT t FROM Tag t WHERE t.status IS NULL OR t.status != 'DELETED'")
    List<Tag> findAllActive();
}
