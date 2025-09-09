package com.otp.whiteboard.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otp.whiteboard.enums.Status;

public record UserUpdateRequest(
        String email,
        Status status,
        @JsonProperty("photo_url") String photoUrl,
        @JsonProperty("display_name") String displayName
) {
}