package com.otp.whiteboard.dto.board;

import jakarta.validation.constraints.NotNull;

public record BoardCreatingRequest(
        @NotNull
    String boardName,
    @NotNull
        Long ownerId
) {}
