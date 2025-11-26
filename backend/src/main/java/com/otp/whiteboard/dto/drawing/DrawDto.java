package com.otp.whiteboard.dto.drawing;

import com.otp.whiteboard.enums.DrawEventType;
import com.otp.whiteboard.enums.DrawingTool;

public record DrawDto(
        String id,
        Long boardId,
        String displayName,
        long timestamp,
        DrawEventType type,
        DrawingTool tool,
        double x,
        double y,
        Long brushSize,
        String brushColor,
        String strokeId
) {

}
