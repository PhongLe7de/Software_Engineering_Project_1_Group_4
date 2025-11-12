package com.otp.whiteboard.dto.board;

import com.otp.whiteboard.model.Board;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.util.List;

public record BoardDto(
        @NotNull
        Long id,

        @NotBlank
        String boardName,

        @NotNull
        Long ownerId,

        @NotEmpty
        List<Long> userIds,

        @NotNull
        @Positive
        Integer amountOfUsers,

        @NotNull
        @PositiveOrZero
        Integer numberOfStrokes,

        @Nullable
        String message
) {
    public BoardDto(Board board) {
        this(
                board.getId(),
                board.getName(),
                board.getOwnerId(),
                board.getUserIds(),
                board.getUserIds().size(),
                board.getNumberOfStrokes(),
                null
        );
    }

    public BoardDto withMessage(String message) {
        return new BoardDto(id, boardName, ownerId, userIds, amountOfUsers, numberOfStrokes, message);
    }
}
