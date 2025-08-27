package com.otp.whiteboard.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DrawEventType {
    START("start"),
    DRAW("draw"),
    END("end");

    private final String value;

    DrawEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DrawEventType fromValue(String value) {
        if (value == null) return null;
        for (DrawEventType type : DrawEventType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DrawEventType: " + value);
    }
}