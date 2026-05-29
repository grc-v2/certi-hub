package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.QuestionBankItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankItemRepository extends JpaRepository<QuestionBankItem, Long> {

    List<QuestionBankItem> findByQuestionBankIdOrderByPositionAsc(Long questionBankId);
}
