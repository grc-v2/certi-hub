package com.certimaster.engagement.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.adapter.web.response.PostResponse;
import com.certimaster.engagement.domain.entity.Post;
import com.certimaster.engagement.adapter.web.mapper.PostMapper;
import com.certimaster.engagement.adapter.persistence.PostReadRepository;
import com.certimaster.engagement.application.query.GetPostsByTagQuery;
import com.certimaster.engagement.application.query.GetTagByIdQuery;
import com.certimaster.engagement.application.query.GetTagBySlugQuery;
import com.certimaster.engagement.application.query.SearchTagsQuery;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import com.certimaster.engagement.domain.entity.PostTag;
import com.certimaster.engagement.adapter.web.mapper.TagMapper;
import com.certimaster.engagement.adapter.persistence.PostTagReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TagQueryService {


    private final PostTagReadRepository tagRepository;
    private final PostReadRepository postRepository;
    private final TagMapper tagMapper;
    private final PostMapper postMapper;

    public TagQueryService(PostTagReadRepository tagRepository,
                           PostReadRepository postRepository,
                           TagMapper tagMapper,
                           PostMapper postMapper) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.tagMapper = tagMapper;
        this.postMapper = postMapper;
    }

    public PageResponse<TagResponse> search(SearchTagsQuery query) {
        Sort sort = createSort(query.sortBy(), query.sortDirection());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);
        Page<PostTag> page = tagRepository.search(query.keyword(), pageable);

        return PageResponse.of(page.map(this::buildTagResponse));
    }

    public TagResponse getTagById(GetTagByIdQuery query) {
        PostTag tag = findTagById(query.id());
        return buildTagResponse(tag);
    }

    public TagResponse getTagBySlug(GetTagBySlugQuery query) {
        PostTag tag = tagRepository.findBySlug(query.slug())
                .orElseThrow(() -> ResourceNotFoundException.byField("Tag", "slug", query.slug()));
        return buildTagResponse(tag);
    }

    public List<TagResponse> getAllTags() {
        return getTagsWithCounts();
    }

    public List<TagResponse> getTagsWithCounts() {
        List<Object[]> results = tagRepository.findAllWithPostCount();

        return results.stream()
                .map(row -> {
                    PostTag tag = (PostTag) row[0];
                    Long postCount = (Long) row[1];
                    TagResponse response = tagMapper.toResponse(tag);
                    response.setPostCount(postCount);
                    return response;
                })
                .collect(Collectors.toList());
    }

    public PageResponse<PostResponse> getPostsByTag(GetPostsByTagQuery query) {
        if (!tagRepository.existsBySlug(query.tagSlug())) {
            throw ResourceNotFoundException.byField("Tag", "slug", query.tagSlug());
        }

        Pageable pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Post> postPage = postRepository.findByTagSlug(query.tagSlug(), PostStatus.PUBLISHED, pageable);

        return PageResponse.of(postPage.map(postMapper::toResponse));
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

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
