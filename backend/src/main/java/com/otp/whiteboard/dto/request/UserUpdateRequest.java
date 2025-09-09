package com.otp.whiteboard.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otp.whiteboard.enums.Status;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        String email,
        Status status,
        @JsonProperty("photo_url") String photoUrl,
        @JsonProperty("display_name") String displayName
) {
}