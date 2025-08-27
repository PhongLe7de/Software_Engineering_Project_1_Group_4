package com.otp.whiteboard.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DrawingTool {
    PEN("pen"),
    ERASER("eraser"),
    HAND("hand");

    private final String value;

    DrawingTool(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DrawingTool fromValue(String value) {
        if (value == null) return null;
        for (DrawingTool tool : DrawingTool.values()) {
            if (tool.value.equalsIgnoreCase(value)) {
                return tool;
            }
        }
        throw new IllegalArgumentException("Unknown DrawingTool: " + value);
    }

    public boolean canDraw() {
        return this == PEN;
    }

    public boolean canErase() {
        return this == ERASER;
    }
}