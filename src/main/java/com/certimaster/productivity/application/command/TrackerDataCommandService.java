package com.certimaster.productivity.application.command;

import com.certimaster.productivity.adapter.persistence.TodoTaskRepository;
import com.certimaster.productivity.adapter.persistence.TrackerCollectionRepository;
import com.certimaster.productivity.adapter.persistence.TrackerResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wipes a user's tracker content — tasks, resources and collections — while keeping
 * their Pomodoro preference (left untouched). Used by the dashboard's "reset data" action.
 */
@Service
@Transactional
public class TrackerDataCommandService {

    private final TodoTaskRepository taskRepository;
    private final TrackerResourceRepository resourceRepository;
    private final TrackerCollectionRepository collectionRepository;

    public TrackerDataCommandService(TodoTaskRepository taskRepository,
                                     TrackerResourceRepository resourceRepository,
                                     TrackerCollectionRepository collectionRepository) {
        this.taskRepository = taskRepository;
        this.resourceRepository = resourceRepository;
        this.collectionRepository = collectionRepository;
    }

    public void resetForUser(Long userId) {
        // Resources first (they reference collections), then collections, then tasks.
        resourceRepository.deleteByUserId(userId);
        collectionRepository.deleteByUserId(userId);
        taskRepository.deleteByUserId(userId);
    }
}
