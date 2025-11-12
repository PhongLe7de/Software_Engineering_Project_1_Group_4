package com.otp.whiteboard.dto.user;

import com.otp.whiteboard.enums.Status;
import jakarta.annotation.Nullable;

public record UserUpdateRequest(
        @Nullable
        String email,

        @Nullable
        Status status,

        @Nullable
        String photoUrl,

        @Nullable
        String displayName,

        @Nullable
        String locale
) {
}