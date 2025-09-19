package com.otp.whiteboard.dto.drawing;

public record CursorDto(
        String username,
        String photoUrl,
        double x,
        double y
) {}
