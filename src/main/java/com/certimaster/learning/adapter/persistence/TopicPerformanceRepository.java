package com.certimaster.learning.adapter.persistence;

import com.certimaster.learning.domain.entity.TopicPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicPerformanceRepository extends JpaRepository<TopicPerformance, Long> {

    List<TopicPerformance> findByExamResultId(Long examResultId);

    List<TopicPerformance> findByExamResultIdIn(List<Long> examResultIds);

    @Query("SELECT tp.topicId, tp.topicName, AVG(tp.percentage), COUNT(tp), MAX(tp.percentage) " +
           "FROM TopicPerformance tp " +
           "WHERE tp.examResult.userId = :userId " +
           "GROUP BY tp.topicId, tp.topicName")
    List<Object[]> getAveragePerformanceByTopicForUser(@Param("userId") Long userId);
}
