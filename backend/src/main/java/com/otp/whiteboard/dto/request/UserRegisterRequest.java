package com.otp.whiteboard.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest (
        @NotBlank
        String email,
        @NotBlank
        String password
){
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}