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
    //Getter methods are automatically provided by the record
    public String getId() {
        return id;
    }
    public Long getBoardId() {
        return boardId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public DrawEventType getType() {
        return type;
    }
    public DrawingTool getTool() {
        return tool;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public Long getBrushSize() {
        return brushSize;
    }
    public String getBrushColor() {
        return brushColor;
    }
    public String getStrokeId() {
        return strokeId;
    }


}
