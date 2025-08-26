package com.otp.whiteboard.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest (
        @NotBlank
        String email
){
    public String getEmail() {
        return email;
    }
}