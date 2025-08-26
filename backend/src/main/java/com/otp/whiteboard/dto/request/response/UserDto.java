package com.otp.whiteboard.dto.request.response;

import com.otp.whiteboard.model.User;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
        @NotBlank String email,
        @NotBlank String username
) {
    public UserDto(final User user) {
        this(
            user.getEmail(),
            user.getUsername()
        );
    }

}