package com.otp.whiteboard.dto.drawing;

public record CursorDto(
        String displayName,
        String photoUrl,
        double x,
        double y
) {}
