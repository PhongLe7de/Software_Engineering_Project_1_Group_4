package com.otp.whiteboard.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest (
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        String photoUrl,

        @NotBlank
        String displayName,

        @NotBlank
        String locale
){}