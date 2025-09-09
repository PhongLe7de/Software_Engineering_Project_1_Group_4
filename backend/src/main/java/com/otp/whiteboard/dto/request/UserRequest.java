package com.otp.whiteboard.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String email,
        @NotBlank String password,
        @JsonProperty("display_name")  @NotBlank String displayName
) {}