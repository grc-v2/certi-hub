package com.certimaster.productivity.application.query;

import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.web.mapper.TaskMapper;
import com.certimaster.productivity.adapter.web.response.TaskResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskQueryService {

    private final TodoTaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskQueryService(TodoTaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<TaskResponse> list(ListTasksQuery query) {
        return taskRepository.findByUserIdOrderByOrderIndexAscCreatedAtDesc(query.userId())
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }
}
