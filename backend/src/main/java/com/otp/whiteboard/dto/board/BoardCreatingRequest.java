package com.otp.whiteboard.dto.board;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BoardCreatingRequest(
        @NotNull
        String boardName,
        @NotNull
        Long ownerId,
        @Nullable
        @Size(max = 500)
        String customMessage
) {}
