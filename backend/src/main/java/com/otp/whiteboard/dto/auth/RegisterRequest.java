package com.otp.whiteboard.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest (
        @NotBlank @Email String email,
        @NotBlank String password,
        @JsonProperty("photo_url") String photoUrl,
        @JsonProperty("display_name") @NotBlank String displayName
){}
