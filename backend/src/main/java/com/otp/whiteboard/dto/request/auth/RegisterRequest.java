package com.otp.whiteboard.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest (
        @NotBlank @Email String email,
        @NotBlank String password,
        @JsonProperty("display_name") @NotBlank String displayName
){}
