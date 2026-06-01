package com.certimaster.engagement.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.domain.entity.PostTagMapping;
import com.certimaster.engagement.adapter.persistence.PostWriteRepository;
import com.certimaster.engagement.adapter.persistence.PostTagMappingRepository;
import com.certimaster.engagement.application.command.AssignTagsToPostCommand;
import com.certimaster.engagement.application.command.CreateTagCommand;
import com.certimaster.engagement.application.command.DeleteTagCommand;
import com.certimaster.engagement.application.command.EnsureTagCommand;
import com.certimaster.engagement.application.command.RemoveAllTagsFromPostCommand;
import com.certimaster.engagement.application.command.UpdateTagCommand;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import com.certimaster.engagement.domain.entity.PostTag;
import com.certimaster.engagement.adapter.web.mapper.TagMapper;
import com.certimaster.engagement.adapter.persistence.PostTagWriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagCommandService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    private final PostTagWriteRepository tagRepository;
    private final PostTagMappingRepository tagMappingRepository;
    private final PostWriteRepository postRepository;
    private final TagMapper tagMapper;

    public TagCommandService(PostTagWriteRepository tagRepository,
                             PostTagMappingRepository tagMappingRepository,
                             PostWriteRepository postRepository,
                             TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMappingRepository = tagMappingRepository;
        this.postRepository = postRepository;
        this.tagMapper = tagMapper;
    }

    public TagResponse createTag(CreateTagCommand command) {
        if (tagRepository.existsByName(command.name())) {
            throw BusinessException.duplicateResource("Tag", command.name());
        }

        PostTag tag = tagMapper.toEntity(command);
        tag.setSlug(generateUniqueSlug(command.name()));

        PostTag savedTag = tagRepository.save(tag);
        return buildTagResponse(savedTag);
    }

    public TagResponse updateTag(UpdateTagCommand command) {
        PostTag tag = findTagById(command.id());

        if (tagRepository.existsByNameAndIdNot(command.name(), command.id())) {
            throw BusinessException.duplicateResource("Tag", command.name());
        }

        tagMapper.updateEntity(tag, command);

        PostTag savedTag = tagRepository.save(tag);
        return buildTagResponse(savedTag);
    }

    public void deleteTag(DeleteTagCommand command) {
        PostTag tag = findTagById(command.id());

        tagMappingRepository.deleteByTagId(command.id());
        tagRepository.delete(tag);
    }

    public void assignTagsToPost(AssignTagsToPostCommand command) {
        if (command.tagNames() == null || command.tagNames().isEmpty()) {
            return;
        }

        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Post", command.postId()));

        tagMappingRepository.deleteByPostId(command.postId());
        post.getTagMappings().clear();

        List<PostTag> existingTags = tagRepository.findByNameIn(command.tagNames());
        Map<String, PostTag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(PostTag::getName, tag -> tag));

        for (String tagName : command.tagNames()) {
            PostTag tag = existingTagMap.get(tagName);

            if (tag == null) {
                tag = PostTag.builder()
                        .name(tagName)
                        .slug(generateUniqueSlug(tagName))
                        .build();
                tag = tagRepository.save(tag);
            }

            PostTagMapping mapping = tagMappingRepository.save(new PostTagMapping(post, tag));
            post.getTagMappings().add(mapping);
        }

    }

    public void removeAllTagsFromPost(RemoveAllTagsFromPostCommand command) {
        tagMappingRepository.deleteByPostId(command.postId());
    }

    public TagResponse ensureTag(EnsureTagCommand command) {
        return tagRepository.findByName(command.name())
                .map(this::buildTagResponse)
                .orElseGet(() -> createTag(new CreateTagCommand(command.name())));
    }

    private PostTag findTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Tag", id));
    }

    private TagResponse buildTagResponse(PostTag tag) {
        TagResponse response = tagMapper.toResponse(tag);
        response.setPostCount(tagRepository.countPublishedPostsByTagId(tag.getId()));
        return response;
    }

    private String generateSlug(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase();
        slug = NON_ALPHANUMERIC.matcher(slug).replaceAll("");
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        slug = MULTIPLE_HYPHENS.matcher(slug).replaceAll("-");
        return slug.replaceAll("^-+|-+$", "");
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = generateSlug(name);

        if (baseSlug.isEmpty()) {
            baseSlug = "tag";
        }

        String slug = baseSlug;
        int suffix = 1;

        while (tagRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + suffix;
            suffix++;
        }

        return slug;
    }
}
