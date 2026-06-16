package com.certimaster.productivity.adapter.persistence;

import com.certimaster.productivity.domain.entity.TodoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {

    List<TodoTask> findByUserIdOrderByOrderIndexAscCreatedAtDesc(Long userId);

    Optional<TodoTask> findByIdAndUserId(Long id, Long userId);

    List<TodoTask> findByUserIdAndIdIn(Long userId, List<Long> ids);

    void deleteByUserId(Long userId);
}
