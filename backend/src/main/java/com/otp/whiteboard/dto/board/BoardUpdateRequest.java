package com.otp.whiteboard.dto.board;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record BoardUpdateRequest (
        String boardName,
        Integer numberOfStrokes,
        @Nullable
        @Size(max = 500)
        String customMessage
){}
