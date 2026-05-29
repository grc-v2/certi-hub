package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionWriteRepository extends JpaRepository<Question, Long> {
}
