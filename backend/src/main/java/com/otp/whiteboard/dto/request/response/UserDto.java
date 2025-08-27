package com.otp.whiteboard.dto.request.response;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.enums.Status;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        @NotBlank String email,
        String displayName,
        String photoUrl,
        @NotBlank Status status,
        LocalDateTime createdAt
) {
    public UserDto(final User user) {
        this(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getPhotoUrl(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }

}