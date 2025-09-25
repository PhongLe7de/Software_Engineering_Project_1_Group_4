package com.otp.whiteboard.dto.board;

public record BoardUpdateRequest (
        String boardName,
        Integer numberOfStrokes
){}
