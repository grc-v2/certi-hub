package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.QuestionTag;
import com.certimaster.learning.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {

    List<QuestionTag> findByQuestionId(Long questionId);

    @Query("SELECT qt.tag FROM QuestionTag qt WHERE qt.question.id = :questionId")
    List<Tag> findTagsByQuestionId(@Param("questionId") Long questionId);

    boolean existsByQuestionIdAndTagId(Long questionId, Long tagId);
}
