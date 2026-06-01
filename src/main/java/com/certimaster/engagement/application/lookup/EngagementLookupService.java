package com.certimaster.engagement.application.lookup;

import com.certimaster.engagement.adapter.persistence.PostTagReadRepository;
import com.certimaster.engagement.domain.entity.ContentType;
import com.certimaster.engagement.domain.entity.DifficultyLevel;
import com.certimaster.engagement.domain.entity.PostStatus;
import com.certimaster.engagement.domain.entity.PostTag;
import com.certimaster.engagement.domain.entity.TagCategory;
import com.certimaster.engagement.domain.entity.VoteType;
import com.certimaster.shared.dto.LookupOption;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EngagementLookupService {

    private final PostTagReadRepository postTagRepository;

    public EngagementLookupService(PostTagReadRepository postTagRepository) {
        this.postTagRepository = postTagRepository;
    }

    public Map<String, List<LookupOption>> constants() {
        Map<String, List<LookupOption>> constants = new LinkedHashMap<>();
        constants.put("postStatuses", enumOptions(PostStatus.class));
        constants.put("postContentTypes", enumOptions(ContentType.class));
        constants.put("postDifficultyLevels", enumOptions(DifficultyLevel.class));
        constants.put("postTagCategories", enumOptions(TagCategory.class));
        constants.put("voteTypes", enumOptions(VoteType.class));
        return constants;
    }

    public Map<String, List<LookupOption>> catalogs() {
        Map<String, List<LookupOption>> catalogs = new LinkedHashMap<>();
        catalogs.put("blogTags", blogTagOptions());
        return catalogs;
    }

    private List<LookupOption> blogTagOptions() {
        return postTagRepository.findAllWithPostCount().stream()
                .map(this::toBlogTagOption)
                .toList();
    }

    private LookupOption toBlogTagOption(Object[] row) {
        PostTag tag = (PostTag) row[0];
        Number postCount = (Number) row[1];
        return LookupOption.of(
                tag.getId().toString(),
                tag.getName(),
                metadata(
                        "id", tag.getId(),
                        "slug", tag.getSlug(),
                        "category", tag.getTagCategory() != null ? tag.getTagCategory().name() : null,
                        "postCount", postCount != null ? postCount.longValue() : 0L,
                        "mappedProviderId", tag.getMappedProviderId(),
                        "mappedCertificationId", tag.getMappedCertificationId(),
                        "mappedDomainId", tag.getMappedDomainId()
                )
        );
    }

    private <E extends Enum<E>> List<LookupOption> enumOptions(Class<E> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                .map(value -> LookupOption.of(value.name(), toLabel(value.name())))
                .toList();
    }

    private String toLabel(String value) {
        String[] parts = value.toLowerCase().split("_");
        StringBuilder label = new StringBuilder();
        for (String part : parts) {
            if (!label.isEmpty()) {
                label.append(' ');
            }
            label.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return label.toString();
    }

    private Map<String, Object> metadata(Object... keyValues) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            Object value = keyValues[i + 1];
            if (value != null) {
                metadata.put((String) keyValues[i], value);
            }
        }
        return metadata;
    }
}
