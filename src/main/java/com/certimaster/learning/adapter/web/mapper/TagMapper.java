package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.learning.application.command.CreateTagCommand;
import com.certimaster.learning.application.command.UpdateTagCommand;
import com.certimaster.learning.adapter.web.response.TagResponse;
import com.certimaster.learning.domain.entity.Tag;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TagMapper {

    public TagResponse toResponse(Tag tag) {
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
                .status(tag.getStatus())
                .build();
    }

    public Tag toEntity(CreateTagCommand command) {
        if (command == null) {
            return null;
        }
        return Tag.builder()
                .name(command.name())
                .status(command.status())
                .createdAt(LocalDateTime.now())
                .createdBy(SecurityUtils.getCurrentUsername())
                .build();
    }

    public void updateEntity(Tag tag, UpdateTagCommand command) {
        if (tag == null || command == null) {
            return;
        }
        if (command.name() != null) {
            tag.setName(command.name());
        }
        if (command.status() != null) {
            tag.setStatus(command.status());
        }
        tag.setUpdatedAt(LocalDateTime.now());
        tag.setUpdatedBy(SecurityUtils.getCurrentUsername());
    }
}
