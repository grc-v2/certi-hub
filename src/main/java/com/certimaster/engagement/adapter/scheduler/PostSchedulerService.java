package com.certimaster.engagement.adapter.scheduler;

import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostSchedulerService {


    private final PostWriteRepository postRepository;

    public PostSchedulerService(PostWriteRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Scheduled(fixedRate = 60000) // every 60 seconds
    @Transactional
    public void publishScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> readyPosts = postRepository.findScheduledPostsReadyToPublish(now);

        for (Post post : readyPosts) {
            post.setStatus(PostStatus.PUBLISHED);
            post.setPublishedAt(now);
            post.setScheduledPublishAt(null);
            postRepository.save(post);
        }
    }
}
