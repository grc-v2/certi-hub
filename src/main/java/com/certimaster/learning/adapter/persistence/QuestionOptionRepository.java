package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    void deleteByQuestionId(Long questionId);
}
