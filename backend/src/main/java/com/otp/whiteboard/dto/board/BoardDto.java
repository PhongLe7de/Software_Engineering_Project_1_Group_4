package com.otp.whiteboard.dto.board;

import com.otp.whiteboard.model.Board;

import java.util.List;

public record BoardDto (
    Long id,
    String boardName,
    Long ownerId,
    List<Long> userIds,
    Integer amountOfUsers,
    Integer numberOfStrokes
){
public BoardDto(Board board) {
    this(
        board.getId(),
        board.getName(),
        board.getOwnerId(),
        board.getUserIds(),
        board.getUserIds().size(),
        board.getNumberOfStrokes()
    );
}
}
