package com.otp.whiteboard.dto.user;

import com.otp.whiteboard.enums.Status;

public record UserUpdateRequest(
        String email,
        Status status,
        String photoUrl,
        String displayName
) {
}