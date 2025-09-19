package com.otp.whiteboard.dto.drawing;

import com.otp.whiteboard.enums.DrawEventType;
import com.otp.whiteboard.enums.DrawingTool;

public record DrawDto(
        String id,
        String userId,
        String username,
        long timestamp,
        DrawEventType type,
        DrawingTool tool,
        double x,
        double y,
        int brushSize,
        String brushColor,
        String strokeId
) {}
