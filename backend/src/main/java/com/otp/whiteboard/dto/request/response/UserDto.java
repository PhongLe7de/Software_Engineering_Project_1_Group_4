package com.otp.whiteboard.dto.request.response;

import com.otp.whiteboard.model.User;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        @NotBlank String email,
        @NotBlank String uid,
        @NotBlank String status
) {
    public UserDto(final User user) {
        this(
            user.getEmail(),
            user.getUid(),
            user.getStatus()
        );
    }

}