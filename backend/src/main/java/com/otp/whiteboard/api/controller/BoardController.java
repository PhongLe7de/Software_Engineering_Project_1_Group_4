package com.otp.whiteboard.api.controller;


import com.otp.whiteboard.dto.board.BoardCreatingRequest;
import com.otp.whiteboard.dto.board.BoardDto;
import com.otp.whiteboard.dto.board.BoardUpdateRequest;
import com.otp.whiteboard.dto.board.ModifyBoardUserRequest;
import com.otp.whiteboard.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.otp.whiteboard.api.Endpoint.BOARD_INTERNAL_API;

@RestController
@RequestMapping(BOARD_INTERNAL_API)
@SecurityRequirement(name = "Bearer Authentication")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @Operation(
            summary = "Create a new board",
            description = """
                    This endpoint creates a new board based on the provided details.
                    It accepts a JSON payload containing the board name and description, and returns the created board's details.
                    """
    )
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<BoardDto> createBoard(
            @RequestBody
                    @NotNull
                    @Valid final
            BoardCreatingRequest request
    ) {
        BoardDto boardDto = boardService.createBoard(request);
        return ResponseEntity.ok(boardDto);
    }

        @Operation(
                summary = "Add a user to a board"
                ,description = """
                This endpoint adds a user to a specified board based on the provided board ID and user ID.
                It accepts the board ID and user ID as request parameters and returns the updated board details.
                """
        )
        @PostMapping("{boardId}/edit/addUser")
        public ResponseEntity<BoardDto> addUserToBoard(
                @PathVariable("boardId") Long boardId,
                @RequestBody
                @NotNull
                @Valid final ModifyBoardUserRequest request
        ) {
            System.out.println(request.toString());
            BoardDto response = boardService.addUserToBoard(boardId, request.userId());
            return ResponseEntity.ok(response);
        }

    @Operation(
            summary = "Remove a user from a board"
            ,description = """
            This endpoint removes a user from a specified board based on the provided board ID and user ID.
            It accepts the board ID and user ID as request parameters and returns the updated board details.
            """
    )
    @PostMapping("{boardId}/edit/removeUser")
    public ResponseEntity<BoardDto> removeUserFromBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody
            @NotNull
            @Valid final ModifyBoardUserRequest request
    ) {
        BoardDto response = boardService.removeUserFromBoard(boardId,request.userId());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update an existing board",
            description = """
                    This endpoint updates the details of an existing board identified by its ID.
                    It accepts a JSON payload containing the updated board information and returns a success message upon completion.
                    """
    )
    @PutMapping("/update/{boardId}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody @NotNull @Valid final BoardUpdateRequest request
    ) {
        BoardDto response = boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get board by ID",
            description = """
                    This endpoint retrieves the details of a board identified by its ID.
                    It returns the board's information as a JSON object.
                    """
    )
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoardById(
            @PathVariable("boardId") Long boardId
    ) {
        BoardDto response = boardService.getBoardById(boardId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all boards",
            description = """
                    This endpoint retrieves a list of all boards.
                    It returns the details of each board as a JSON array.
                    """
    )
    @GetMapping("/all")
    public ResponseEntity<List<BoardDto>> getAllBoards() {
        List<BoardDto> response = boardService.getAllBoards();
        return ResponseEntity.ok(response);
    }

}
