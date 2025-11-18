package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.board.BoardCreatingRequest;
import com.otp.whiteboard.dto.board.BoardDto;
import com.otp.whiteboard.dto.board.BoardUpdateRequest;
import com.otp.whiteboard.dto.board.ModifyBoardUserRequest;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.CustomUserDetails;
import com.otp.whiteboard.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.otp.whiteboard.api.Endpoint.BOARD_INTERNAL_API;

@RestController
@Tag(name = "Board Management", description = "APIs for managing boards")
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
            @Valid final BoardCreatingRequest request
    ) {
        final BoardDto boardDto = boardService.createBoard(request);
        return ResponseEntity.ok(boardDto);
    }

    @Operation(
            summary = "Add a user to a board"
            , description = """
            This endpoint adds a user to a specified board based on the provided board ID and user ID.
            It accepts the board ID and user ID as request parameters and returns the updated board details.
            """
    )
    @PostMapping("{boardId}/edit/addUser")
    public ResponseEntity<BoardDto> addUserToBoard(
            @PathVariable("boardId") final Long boardId,
            @RequestBody
            @NotNull
            @Valid final ModifyBoardUserRequest request,
            @AuthenticationPrincipal @Valid final CustomUserDetails currentUserDetails


    ) {
        final User currentUser = currentUserDetails != null ? currentUserDetails.user() : null;
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        final BoardDto response = boardService.addUserToBoard(boardId, request.userId(), currentUser);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remove a user from a board"
            , description = """
            This endpoint removes a user from a specified board based on the provided board ID and user ID.
            It accepts the board ID and user ID as request parameters and returns the updated board details.
            """
    )
    @PostMapping("{boardId}/edit/removeUser")
    public ResponseEntity<BoardDto> removeUserFromBoard(
            @PathVariable("boardId") final Long boardId,
            @RequestBody
            @NotNull
            @Valid final ModifyBoardUserRequest request
    ) {
        final BoardDto response = boardService.removeUserFromBoard(boardId, request.userId());
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
            @PathVariable("boardId") final Long boardId,
            @RequestBody @NotNull @Valid final BoardUpdateRequest request
    ) {
        final BoardDto response = boardService.updateBoard(boardId, request);
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
            @PathVariable("boardId") final Long boardId,
            @AuthenticationPrincipal @Valid final CustomUserDetails currentUserDetails
    ) {
        final User currentUser = currentUserDetails != null ? currentUserDetails.user() : null;
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        final BoardDto response = boardService.getBoardById(boardId, currentUser);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all boards",
            description = """
                    This endpoint retrieves a list of all boards.
                    It returns the details of each board as a JSON array.
                    """
    )

    @GetMapping(value ="/all", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<BoardDto>> getAllBoards(
            @AuthenticationPrincipal @Valid final CustomUserDetails currentUserDetails
    ) {
        final User currentUser = currentUserDetails != null ? currentUserDetails.user() : null;
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        final List<BoardDto> response = boardService.getAllBoards(currentUser);
        return ResponseEntity.ok(response);
    }

}
