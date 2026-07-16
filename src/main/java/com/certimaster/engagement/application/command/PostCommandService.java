package com.certimaster.engagement.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.web.mapper.PostMapper;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.domain.event.PostPublishedEvent;
import com.certimaster.engagement.domain.service.ProcessedContent;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostTagMapping;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.domain.entity.PostTag;
import com.certimaster.engagement.domain.service.PostContentProcessor;
import com.certimaster.engagement.application.command.AssignTagsToPostCommand;
import com.certimaster.engagement.application.command.RemoveAllTagsFromPostCommand;
import com.certimaster.engagement.application.command.TagCommandService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostCommandService {


    private final PostWriteRepository postRepository;
    private final PostMapper postMapper;
    private final SlugService slugService;
    private final PostContentProcessor contentProcessor;
    private final TagCommandService tagCommandService;
    private final ApplicationEventPublisher applicationEvents;

    public PostCommandService(PostWriteRepository postRepository,
                              PostMapper postMapper,
                              SlugService slugService,
                              PostContentProcessor contentProcessor,
                              TagCommandService tagCommandService,
                              ApplicationEventPublisher applicationEvents) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.slugService = slugService;
        this.contentProcessor = contentProcessor;
        this.tagCommandService = tagCommandService;
        this.applicationEvents = applicationEvents;
    }

    public PostResponse createPost(CreatePostCommand command) {
        Post post = postMapper.toEntity(command);
        post.setAuthorId(command.authorId());
        post.setSlug(slugService.generateUniqueSlug(command.title()));
        post.setStatus(PostStatus.DRAFT);

        processContent(post, command.content());
        applySeoDefaults(post, command);
        initializeCounters(post);

        Post saved = postRepository.save(post);
        synchronizeTags(saved.getId(), command.tagNames());
        return postMapper.toResponse(saved);
    }

    public PostResponse updatePost(UpdatePostCommand command) {
        Post post = findPostById(command.id());
        checkPostOwnership(post, command.userId());

        postMapper.updateEntity(post, command);
        processContent(post, command.content());
        applySeoDefaults(post, command);

        Post saved = postRepository.save(post);
        synchronizeTags(saved.getId(), command.tagNames());
        return postMapper.toResponse(saved);
    }

    public PostResponse publishPost(PublishPostCommand command) {
        Post post = findPostById(command.id());
        checkPostOwnership(post, command.userId());
        return doPublish(post);
    }

    /** Admin moderation: publish any post regardless of authorship. */
    public PostResponse adminPublishPost(Long id) {
        return doPublish(findPostById(id));
    }

    private PostResponse doPublish(Post post) {
        if (post.getStatus() == PostStatus.PUBLISHED) {
            throw BusinessException.operationFailed("Post is already published");
        }
        if (post.getStatus() == PostStatus.ARCHIVED) {
            throw BusinessException.operationFailed("Cannot publish an archived post. Restore it first.");
        }

        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());
        post.setScheduledPublishAt(null);

        Post saved = postRepository.save(post);
        applicationEvents.publishEvent(toPostPublishedEvent(saved));
        return postMapper.toResponse(saved);
    }

    public PostResponse schedulePost(SchedulePostCommand command) {
        Post post = findPostById(command.id());
        checkPostOwnership(post, command.userId());

        if (command.scheduledAt() == null || command.scheduledAt().isBefore(LocalDateTime.now())) {
            throw BusinessException.invalidInput("Scheduled publish date must be in the future");
        }
        if (post.getStatus() == PostStatus.PUBLISHED) {
            throw BusinessException.operationFailed("Cannot schedule an already published post");
        }
        if (post.getStatus() == PostStatus.ARCHIVED) {
            throw BusinessException.operationFailed("Cannot schedule an archived post. Restore it first.");
        }

        post.setStatus(PostStatus.SCHEDULED);
        post.setScheduledPublishAt(command.scheduledAt());

        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public PostResponse archivePost(ArchivePostCommand command) {
        Post post = findPostById(command.id());
        checkPostOwnership(post, command.userId());
        return doArchive(post);
    }

    /** Admin moderation: archive any post regardless of authorship. */
    public PostResponse adminArchivePost(Long id) {
        return doArchive(findPostById(id));
    }

    private PostResponse doArchive(Post post) {
        post.setStatus(PostStatus.ARCHIVED);

        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public void deletePost(DeletePostCommand command) {
        Post post = findPostById(command.id());
        checkPostOwnership(post, command.userId());

        postRepository.delete(post);
    }

    /** Admin moderation: delete any post regardless of authorship. */
    public void adminDeletePost(Long id) {
        postRepository.delete(findPostById(id));
    }

    public PostResponse submitForReview(SubmitPostForReviewCommand command) {
        Post post = findPostById(command.id());
        checkPostOwnership(post, command.userId());

        if (post.getStatus() != PostStatus.DRAFT) {
            throw BusinessException.operationFailed("Only draft posts can be submitted for review");
        }

        post.setStatus(PostStatus.IN_REVIEW);

        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public PostResponse featurePost(FeaturePostCommand command) {
        Post post = findPostById(command.id());
        post.setFeatured(command.featured());
        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public PostResponse pinPost(PinPostCommand command) {
        Post post = findPostById(command.id());
        post.setPinned(command.pinned());
        Post saved = postRepository.save(post);
        return postMapper.toResponse(saved);
    }

    public void incrementViewCount(IncrementPostViewCountCommand command) {
        if (!postRepository.existsById(command.id())) {
            throw ResourceNotFoundException.byId("Post", command.id());
        }
        postRepository.incrementViewCount(command.id());
    }

    private Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", id));
    }

    private void checkPostOwnership(Post post, Long userId) {
        if (!post.getAuthorId().equals(userId)) {
            throw ForbiddenException.resourceOwnerOnly();
        }
    }

    private void processContent(Post post, String content) {
        ProcessedContent processed = contentProcessor.process(content);
        post.setContentHtml(processed.contentHtml());
        post.setTableOfContents(processed.tocJson());
        post.setWordCount(processed.wordCount());
        post.setReadingTimeMinutes(processed.readingTimeMinutes());
        if (post.getExcerpt() == null || post.getExcerpt().isBlank()) {
            post.setExcerpt(processed.excerpt());
        }
    }

    private void applySeoDefaults(Post post, CreatePostCommand command) {
        if (command.seoTitle() == null || command.seoTitle().isBlank()) {
            post.setSeoTitle(command.title());
        }
        if (command.seoDescription() == null || command.seoDescription().isBlank()) {
            post.setSeoDescription(post.getExcerpt());
        }
    }

    private void applySeoDefaults(Post post, UpdatePostCommand command) {
        if (command.seoTitle() == null || command.seoTitle().isBlank()) {
            post.setSeoTitle(command.title());
        }
        if (command.seoDescription() == null || command.seoDescription().isBlank()) {
            post.setSeoDescription(post.getExcerpt());
        }
    }

    private void initializeCounters(Post post) {
        post.setViewsCount(0);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setUniqueViewCount(0);
        post.setBookmarkCount(0);
        post.setShareCount(0);
        post.setWordCount(post.getWordCount() != null ? post.getWordCount() : 0);
    }

    private void synchronizeTags(Long postId, List<String> tagNames) {
        if (tagNames == null) {
            return;
        }
        if (tagNames.isEmpty()) {
            tagCommandService.removeAllTagsFromPost(new RemoveAllTagsFromPostCommand(postId));
            return;
        }
        tagCommandService.assignTagsToPost(new AssignTagsToPostCommand(postId, tagNames));
    }

    private PostPublishedEvent toPostPublishedEvent(Post post) {
        PostPublishedEvent event = new PostPublishedEvent();
        event.setPostId(post.getId());
        event.setTitle(post.getTitle());
        event.setSlug(post.getSlug());
        event.setContentType(post.getContentType() != null ? post.getContentType().name() : null);
        event.setDifficultyLevel(post.getDifficultyLevel() != null ? post.getDifficultyLevel().name() : null);
        event.setAuthorId(post.getAuthorId());
        LocalDateTime publishedAt = post.getPublishedAt() != null ? post.getPublishedAt() : LocalDateTime.now();
        event.setOccurredAt(publishedAt);
        event.setPublishedAt(publishedAt);

        if (post.getTagMappings() != null) {
            event.setTagNames(post.getTagMappings().stream()
                    .map(PostTagMapping::getTag)
                    .filter(Objects::nonNull)
                    .map(PostTag::getName)
                    .collect(Collectors.toList()));
        }

        if (post.getCertRefs() != null && !post.getCertRefs().isEmpty()) {
            event.setCertificationIds(post.getCertRefs().stream()
                    .map(this::parseLongOrNull)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList()));
        }

        return event;
    }

    private Long parseLongOrNull(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
