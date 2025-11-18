package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.board.BoardCreatingRequest;
import com.otp.whiteboard.dto.board.BoardDto;
import com.otp.whiteboard.dto.board.BoardUpdateRequest;
import com.otp.whiteboard.dto.board.ModifyBoardUserRequest;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.CustomUserDetails;
import com.otp.whiteboard.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardControllerTest {

    private BoardController boardController;

    private static final String SAMPLE_BOARD_NAME = "Sample Board";
    private static final String UPDATE_BOARD_NAME = "Updated Board Name";
    private static final String SUCCESS_MESSAGE = "Board created successfully";
    private static final String SUCCESS_ADD_USER_MESSAGE = "User added successfully";
    private static final String SUCCESS_REMOVE_USER_MESSAGE = "User removed successfully";
    private static final String SUCCESS_UPDATE_BOARD_MESSAGE = "Board updated successfully";

    private static final Long OWNER_ID = 1L;
    private static final Long BOARD_ID = 2L;
    private static final Long USER_ID = 3L;
    private static final Integer AMOUNT_OF_USERS = 5;
    private static final Integer NUMBER_OF_STROKES = 10;

    @Mock
    private BoardService boardService;

    @BeforeEach
    void init() {
        setupMockData();
        setupTestTarget();
    }

    private void setupMockData() {
        when(boardService.createBoard(any(BoardCreatingRequest.class))).thenAnswer(invocation -> {
            final BoardCreatingRequest request = invocation.getArgument(0);
            return new BoardDto(
                    BOARD_ID,
                    request.boardName(),
                    request.ownerId(),
                    List.of(request.ownerId()),
                    AMOUNT_OF_USERS,
                    NUMBER_OF_STROKES,
                    SUCCESS_MESSAGE

            );
        });

        when(boardService.addUserToBoard(any(Long.class), any(Long.class))).thenAnswer(invocation -> {
            final Long boardId = invocation.getArgument(0);
            final Long userId = invocation.getArgument(1);
            return new BoardDto(
                    boardId,
                    SAMPLE_BOARD_NAME,
                    OWNER_ID,
                    List.of(OWNER_ID, userId),
                    AMOUNT_OF_USERS,
                    NUMBER_OF_STROKES,
                    SUCCESS_ADD_USER_MESSAGE
            );
        });

        when(boardService.removeUserFromBoard(any(Long.class), any(Long.class))).thenAnswer(invocation -> {
            final Long boardId = invocation.getArgument(0);
            return new BoardDto(
                    boardId,
                    SAMPLE_BOARD_NAME,
                    OWNER_ID,
                    List.of(OWNER_ID),
                    AMOUNT_OF_USERS,
                    NUMBER_OF_STROKES,
                    SUCCESS_REMOVE_USER_MESSAGE
            );
        });

        when(boardService.updateBoard(any(Long.class), any(BoardUpdateRequest.class))).thenAnswer(invocation -> {
            final Long boardId = invocation.getArgument(0);
            final BoardUpdateRequest request = invocation.getArgument(1);
            return new BoardDto(
                    boardId,
                    request.boardName(),
                    OWNER_ID,
                    List.of(OWNER_ID),
                    AMOUNT_OF_USERS,
                    NUMBER_OF_STROKES,
                    SUCCESS_UPDATE_BOARD_MESSAGE
            );
        });

        when(boardService.getBoardById(any(Long.class), any(User.class))).thenAnswer(invocation -> {
            final Long boardId = invocation.getArgument(0);
            return new BoardDto(
                    boardId,
                    SAMPLE_BOARD_NAME,
                    OWNER_ID,
                    List.of(OWNER_ID),
                    AMOUNT_OF_USERS,
                    NUMBER_OF_STROKES,
                    SUCCESS_MESSAGE
            );
        });

        when(boardService.getAllBoards(any(User.class))).thenReturn(List.of(
                new BoardDto(
                        BOARD_ID,
                        SAMPLE_BOARD_NAME,
                        OWNER_ID,
                        List.of(OWNER_ID),
                        AMOUNT_OF_USERS,
                        NUMBER_OF_STROKES,
                        SUCCESS_MESSAGE
                )
        ));

    }

    private void setupTestTarget() {
        boardController = new BoardController(boardService);
    }


    @DisplayName("As a user, I want to create a new board, so that I can start collaborating with others.")
    @Test
    void createBoardReturnBoardDto() {
        // Given
        final BoardCreatingRequest request = new BoardCreatingRequest(SAMPLE_BOARD_NAME, OWNER_ID);
        // When & Then
        try {
            ResponseEntity<BoardDto> boardDto = boardController.createBoard(request);

            assertNotNull(boardDto);
            assertNotNull(boardDto.getBody());
            assertEquals(BOARD_ID, boardDto.getBody().id());
            assertEquals(SAMPLE_BOARD_NAME, boardDto.getBody().boardName());
            assertEquals(OWNER_ID, boardDto.getBody().ownerId());
            assertEquals(AMOUNT_OF_USERS, boardDto.getBody().amountOfUsers());
            assertEquals(NUMBER_OF_STROKES, boardDto.getBody().numberOfStrokes());
            assertEquals("Board created successfully", boardDto.getBody().message());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("As a user, I want to add another user to my board, so that we can collaborate together.")
    @Test
    void addUserToBoard() {
        // Given
        final Long NEW_USER_ID = 9L;
        final ModifyBoardUserRequest request = new ModifyBoardUserRequest(NEW_USER_ID);
        // When & Then
        try {
            final ResponseEntity<BoardDto> response = boardController.addUserToBoard(BOARD_ID, request);
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(BOARD_ID, response.getBody().id());
            assertEquals(SAMPLE_BOARD_NAME, response.getBody().boardName());
            assertEquals(OWNER_ID, response.getBody().ownerId());
            assertEquals(AMOUNT_OF_USERS, response.getBody().amountOfUsers());
            assertEquals(NUMBER_OF_STROKES, response.getBody().numberOfStrokes());
            assertEquals("User added successfully", response.getBody().message());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("As a user, I want to remove a user from my board, so that I can manage access to my board.")
    @Test
    void removeUserFromBoard() {
        // Given
        final ModifyBoardUserRequest request = new ModifyBoardUserRequest(2L);
        // When & Then
        try {
            final ResponseEntity<BoardDto> response = boardController.removeUserFromBoard(BOARD_ID, request);
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(BOARD_ID, response.getBody().id());
            assertEquals(SAMPLE_BOARD_NAME, response.getBody().boardName());
            assertEquals(OWNER_ID, response.getBody().ownerId());
            assertEquals(AMOUNT_OF_USERS, response.getBody().amountOfUsers());
            assertEquals(NUMBER_OF_STROKES, response.getBody().numberOfStrokes());
            assertEquals("User removed successfully", response.getBody().message());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("As a user, I want to update board details, so that I can keep information current.")
    @Test
    void updateBoard() {
        final BoardUpdateRequest request = new BoardUpdateRequest(
                UPDATE_BOARD_NAME,
                NUMBER_OF_STROKES
        );
        try {
            final ResponseEntity<BoardDto> response = boardController.updateBoard(BOARD_ID, request);

            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(BOARD_ID, response.getBody().id());
            assertEquals(UPDATE_BOARD_NAME, response.getBody().boardName());
            assertEquals(OWNER_ID, response.getBody().ownerId());
            assertEquals(AMOUNT_OF_USERS, response.getBody().amountOfUsers());
            assertEquals(NUMBER_OF_STROKES, response.getBody().numberOfStrokes());
            assertEquals("Board updated successfully", response.getBody().message());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("As a user, I want to retrieve error details when accessing a board fails because user is null")
    @Test
    void getBoardByIdReturnsErrorWhenUserIsNull() {
        // Given
        final Long boardId = 1L;
        // When & Then
        try {
            final ResponseEntity<BoardDto> response = boardController.getBoardById(boardId, null);
            assertEquals(401, response.getStatusCodeValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("As a user, I want to retrieve a board by its ID, so that I can view its details.")
    @Test
    void getBoardById() {
        // Given
        final User currentUser = new User();
        currentUser.setId(USER_ID);
        final CustomUserDetails user = new CustomUserDetails(currentUser);

        // When & Then
        try {
            final ResponseEntity<BoardDto> response = boardController.getBoardById(BOARD_ID, user);
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(BOARD_ID, response.getBody().id());
            assertEquals(SAMPLE_BOARD_NAME, response.getBody().boardName());
            assertEquals(OWNER_ID, response.getBody().ownerId());
            assertEquals(AMOUNT_OF_USERS, response.getBody().amountOfUsers());
            assertEquals(NUMBER_OF_STROKES, response.getBody().numberOfStrokes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("As a user, I want to retrieve error details when accessing a board fails because user is null")
    @Test
    void getAllBoardReturnsErrorWhenUserIsNull() {
        // When & Then
        final ResponseEntity<List<BoardDto>> response = boardController.getAllBoards(null);
        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @DisplayName("As a user, I want to retrieve all boards, so that I can see an overview of my projects.")
    @Test
    void getAllBoards() {
        // Given
        final User currentUser = new User();
        currentUser.setId(1L);
        final CustomUserDetails user = new CustomUserDetails(currentUser);
        // When & Then
        try {
            final ResponseEntity<List<BoardDto>> response = boardController.getAllBoards(user);
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}