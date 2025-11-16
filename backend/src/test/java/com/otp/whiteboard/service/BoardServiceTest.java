package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.board.BoardCreatingRequest;
import com.otp.whiteboard.dto.board.BoardDto;
import com.otp.whiteboard.dto.board.BoardUpdateRequest;
import com.otp.whiteboard.enums.Role;
import com.otp.whiteboard.model.Board;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.model.UserBoard;
import com.otp.whiteboard.repository.BoardRepository;
import com.otp.whiteboard.repository.UserBoardRepository;
import com.otp.whiteboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceTest {
    private BoardService boardService;

    private static final String BOARD_NAME = "Test Board";
    private static final String EXIT_BOARD_NAME = "Exit Board";
    private static final String UPDATED_BOARD_NAME = "Updated Board Name";
    private static final Long NONEXIT_USER = 99L;
    private static final Long OWNER_ID = 1L;
    private static final Long BOARD_ID = 2L;
    private static final Long NONEXIT_BOARD_ID = 99L;
    private static final Long EXIT_BOARD_ID = 10L;
    private static final Long USER_ID = 3L;
    private static final Integer NUMBER_OF_STROKES = 5;

    @Mock
    private UserService mockUserService;

    @Mock
    private BoardRepository mockBoardRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private UserBoardRepository mockUserBoardRepository;

    @Mock
    private UserService mockLocalizationService;

    @Mock
    private LocalizationService mockLocalizationService2;

    private Board testBoard;
    private User testUser;
    private UserBoard testUserBoard;

    @BeforeEach
    void init(){
        setupMockData();
        setupMocks();
        setupTestTarget();
    }

    void setupMockData(){
        testBoard = new Board();
        testBoard.setId(BOARD_ID);
        testBoard.setName(BOARD_NAME);
        testBoard.setOwnerId(OWNER_ID);
        testBoard.setNumberOfStrokes(NUMBER_OF_STROKES);

        testUser = new User();
        testUser.setId(USER_ID);

        testUserBoard = new UserBoard();
        testUserBoard.setId(1L);
        testUserBoard.setBoard(testBoard);
        testUserBoard.setUser(testUser);
        testUserBoard.setRole(Role.EDITOR);
    }

    void setupMocks(){
        when(mockBoardRepository.save(any(Board.class)))
                .thenAnswer(invocation ->{
                    Board board = invocation.getArgument(0);
                    if(board.getId() == null){
                        board.setId(BOARD_ID);
                    }
                    return board;
                });
        when(mockBoardRepository.findAll()).thenReturn(List.of(testBoard));
        when(mockBoardRepository.findById(BOARD_ID))
                .thenReturn(Optional.ofNullable(testBoard));
        when(mockBoardRepository.findBoardsByName(EXIT_BOARD_NAME))
                .thenReturn(java.util.Optional.of(new Board()));
        when(mockUserRepository.findById(NONEXIT_USER))
                .thenReturn(Optional.empty());
        when(mockBoardRepository.findBoardsByName(BOARD_NAME))
                .thenReturn(Optional.empty());
        when(mockUserRepository.findById(OWNER_ID))
                .thenReturn(Optional.ofNullable(testUser));
        when(mockUserRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(testUser));
        when(mockUserBoardRepository.save(any(UserBoard.class)))
                .thenAnswer(invocation -> {
                    UserBoard userBoard = invocation.getArgument(0);
                    if(userBoard.getId() == null){
                        userBoard.setId(1L);
                    }
                    return userBoard;
                });
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(BOARD_ID, USER_ID))
                .thenReturn(testUserBoard);
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(BOARD_ID, USER_ID))
                .thenReturn(testUserBoard);
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(NONEXIT_BOARD_ID, USER_ID))
                .thenReturn(null);
    }
    void setupTestTarget(){
        boardService = new BoardService(mockBoardRepository, mockUserRepository, mockUserBoardRepository, mockUserService, mockLocalizationService2);
    }

    @DisplayName("Attempt to create a board with an existing name should throw an exception")
    @Test
    void createBoardWithExistingNameShouldThrowException() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest(EXIT_BOARD_NAME, OWNER_ID);
        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boardService.createBoard(request);
        });
        assertEquals("Board already exists with name: " + EXIT_BOARD_NAME, exception.getMessage());
    }

    @DisplayName("Attempt to create a board with a non-existing owner should throw an exception")
    @Test
    void createBoardWithNonExistingOwnerShouldThrowException() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest(BOARD_NAME, NONEXIT_USER);
        //when & then
        try{
            boardService.createBoard(request);
        }catch(IllegalArgumentException e){
            assertEquals("Owner not found", e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As user, I want to create a board with valid data, so that I can start collaborating.")
    @Test
    void createBoard() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest(BOARD_NAME, OWNER_ID);
        //when & then
        try{
            boardService.createBoard(request);
        } catch (Exception e ){
            assertEquals("Board name is already exists", e.getMessage());
        }
    }

    @DisplayName("As user, I want to get an empty list when retrieving all boards if none exist.")
    @Test
    void getAllBoardsWhenNoneExistShouldReturnEmptyList() {
        //given
        when(mockBoardRepository.findAll()).thenReturn(List.of());

        //when
        final List<BoardDto> boards = boardService.getAllBoards(testUser);

        //then
        assertNotNull(boards);
        assertTrue(boards.isEmpty());
    }

    @DisplayName("As user, I want to retrieve a board by its ID, so that I can view or edit it.")
    @Test
    void getAllBoards() {
        final List<BoardDto> boards = boardService.getAllBoards(testUser);
        //then
        assertNotNull(boards);
        assertEquals(1, boards.size());
    }

    @DisplayName("As user, I want to retrieve a board by a non-existing ID, so that I receive an error message.")
    @Test
    void getBoardByNotExistingIdShouldThrowException() {
        //given
        final Long notFoundId = 3L;
        //When & Then
        try {
            boardService.getBoardById(notFoundId);
        } catch (IllegalArgumentException e) {
            assertEquals("Board not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As user, I want to retrieve a board by its ID, so that I can view or edit it.")
    @Test
    void getBoardById() {
        //given
        final Long boardId = BOARD_ID;
        //when
        final BoardDto result = boardService.getBoardById(boardId);
        //then
        assertNotNull(result);
        assertEquals(boardId, result.id());
    }

    @DisplayName("As an admin, I want to get error when adding a user to a board with non-existing board ID")
    @Test
    void addUserToBoardWithNotExistingBoardIdShouldThrowException() {
        //given
        final Long notFoundId = 3L;
        final Long userId = USER_ID;
        //When & Then
        try {
            boardService.addUserToBoard(notFoundId, userId);
        } catch (IllegalArgumentException e) {
            assertEquals("Board not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As an admin, I want to get error when adding a user to a board with non-existing user ID")
    @Test
    void addUserToBoardWithNotExistingUserIdShouldThrowException() {
        //given
        final Long boardId = BOARD_ID;
        final Long notFoundId = NONEXIT_USER;
        //When & Then
        try {
            boardService.addUserToBoard(boardId, notFoundId);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As an admin, I want to get error when adding a user to a board if the user is already in the board")
    @Test
    void addUserToBoardWithUserAlreadyInBoardShouldThrowException() {
        //given
        final Long boardId = BOARD_ID;
        final Long userId = USER_ID;
        //When &
        when(mockUserRepository.findById(USER_ID))
                .thenReturn(Optional.ofNullable(testUser));
        // Then
        try {
            boardService.addUserToBoard(boardId, userId);
        } catch (IllegalArgumentException e) {
            assertEquals("User with ID: " + userId + " already exists in the board", e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As an admin, I want to add a user to a board")
    @Test
    void addUserToBoardShouldSucceed() {
        //given
        final Long boardId = BOARD_ID;
        final Long userId = 10L;
        final User newUser = new User();
        newUser.setId(userId);
        when(mockUserRepository.findById(userId))
                .thenReturn(Optional.ofNullable(newUser));
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(boardId, userId))
                .thenReturn(null);
        //when & then
        try{
            boardService.addUserToBoard(boardId, userId);
            verify(mockBoardRepository, times(1)).save(any(Board.class));
        } catch (Exception e ){
            fail("Should have succeeded but didn't");
        }
    }


    @DisplayName("As an admin, I want to get error when removing a user from a board with non-existing user ID")
    @Test
    void removeUserFromBoardWithNotExistingUserIdShouldThrowException() {
        //given
        final Long boardId = BOARD_ID;
        final Long userId = NONEXIT_USER;
        //When & Then
        try {
            boardService.removeUserFromBoard(boardId, userId);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with ID: " + userId, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As an admin, I want to get error when removing a user from a board with non-existing board ID")
    @Test
    void removeUserFromBoardWithNotExistingBoardIdShouldThrowException() {
        //given
        final Long notFoundId = NONEXIT_BOARD_ID;
        final Long userId = USER_ID;
        //When & Then
        try {
            boardService.removeUserFromBoard(notFoundId, userId);
        } catch (IllegalArgumentException e) {
            assertEquals(String.format(
                    "User with ID: %d is not associated with board ID: %d",
                    userId, notFoundId
            ), e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As an admin, I want to remove a user from a board")
    @Test
    void removeUserFromBoardShouldSucceed() {
        //given
        final Long boardId = BOARD_ID;
        final Long userId = USER_ID;
        //when & then
        try{
            boardService.removeUserFromBoard(boardId, userId);
            verify(mockUserBoardRepository, times(1)).delete(testUserBoard);
        } catch (Exception e ){
            fail("Should have succeeded but didn't");
        }
    }
    @DisplayName("As a user, I want to get error when updating a board with non-existing ID")
    @Test
    void updateBoardWithNotExistingIdShouldThrowException() {
        //given
        final Long notFoundId = 3L;
        final String boardName = UPDATED_BOARD_NAME;
        final Integer numberOfStrokes = NUMBER_OF_STROKES;
        final BoardUpdateRequest request = new BoardUpdateRequest(boardName, numberOfStrokes);
        //When & Then
        try {
            boardService.updateBoard(notFoundId, request);
        } catch (IllegalArgumentException e) {
            assertEquals("Board not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As a user, I want to update a board with valid data")
    @Test
    void updateBoard() {
        //given
        final Long boardId = BOARD_ID;
        final String boardName = UPDATED_BOARD_NAME;
        final Integer numberOfStrokes = NUMBER_OF_STROKES;
        final BoardUpdateRequest request = new BoardUpdateRequest(boardName, numberOfStrokes);
        //when
        final BoardDto result = boardService.updateBoard(boardId, request);
        //then
        assertNotNull(result);
        assertEquals(boardId, result.id());
        assertEquals(UPDATED_BOARD_NAME, result.boardName());
        assertEquals(NUMBER_OF_STROKES, result.numberOfStrokes());

    }
}