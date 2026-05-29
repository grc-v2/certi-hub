package com.certimaster.identity.application.command;

import org.springframework.web.multipart.MultipartFile;

public record UploadAvatarCommand(
        Long userId,
        MultipartFile file
) {
}
