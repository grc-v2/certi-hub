package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    Optional<UserAnswer> findByUserExamSessionIdAndQuestion_Id(Long sessionId, Long questionId);

    List<UserAnswer> findByUserExamSessionId(Long sessionId);

}
