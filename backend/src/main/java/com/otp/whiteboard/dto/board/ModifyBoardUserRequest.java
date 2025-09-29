package com.otp.whiteboard.dto.board;

import jakarta.validation.constraints.NotNull;

public record ModifyBoardUserRequest (
        @NotNull
        Long userId
){}
