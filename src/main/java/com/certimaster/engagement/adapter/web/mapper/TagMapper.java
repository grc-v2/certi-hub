package com.certimaster.engagement.adapter.web.mapper;

import com.certimaster.engagement.application.command.CreateTagCommand;
import com.certimaster.engagement.application.command.UpdateTagCommand;
import com.certimaster.engagement.adapter.web.response.TagResponse;
import com.certimaster.engagement.domain.entity.PostTag;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between PostTag entities and DTOs.
 * Provides null-safe mapping operations.
 */
@Component
public class TagMapper {

    /**
     * Converts a PostTag entity to TagResponse DTO.
     * Note: postCount must be set separately as it requires a query.
     *
     * @param tag the tag entity
     * @return the tag response DTO, or null if input is null
     */
    public TagResponse toResponse(PostTag tag) {
        if (tag == null) {
            return null;
        }
        return TagResponse.builder()
                .id(tag.getId())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .createdBy(tag.getCreatedBy())
                .updatedBy(tag.getUpdatedBy())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }

    /**
     * Converts a command object to PostTag entity.
     * Note: slug must be generated separately.
     *
     * @param command the command object
     * @return the tag entity, or null if input is null
     */
    public PostTag toEntity(CreateTagCommand command) {
        if (command == null) {
            return null;
        }
        return PostTag.builder()
                .name(command.name())
                .build();
    }

    /**
     * Updates an existing PostTag entity with data from a command object.
     * Only updates non-null fields from the command (partial update).
     *
     * @param tag the tag entity to update
     * @param command the command object with new data
     */
    public void updateEntity(PostTag tag, UpdateTagCommand command) {
        if (tag == null || command == null) {
            return;
        }
        if (command.name() != null) {
            tag.setName(command.name());
        }
    }
}
