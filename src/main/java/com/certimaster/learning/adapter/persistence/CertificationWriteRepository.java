package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificationWriteRepository extends JpaRepository<Certification, Long> {

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT c FROM Certification c WHERE c.id = :id AND c.status != 'DELETED'")
    Optional<Certification> findActiveById(@Param("id") Long id);
}
