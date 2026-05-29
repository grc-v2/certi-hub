package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamResultWriteRepository extends JpaRepository<ExamResult, Long> {

    Optional<ExamResult> findBySessionId(Long sessionId);
}
