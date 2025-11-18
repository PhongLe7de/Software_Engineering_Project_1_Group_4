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
import com.otp.whiteboard.security.CustomUserDetails;
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
    private static final Long NON_EXIST_USER = 99L;
    private static final Long OWNER_ID = 1L;
    private static final Long BOARD_ID = 2L;
    private static final Long NON_EXIST_BOARD_ID = 99L;
    private static final Long USER_ID = 3L;
    private static final Integer NUMBER_OF_STROKES = 5;

    private static final String FAIL_MESSAGE = "Should have failed but didn't";
    private static final String BOARD_NOT_FOUND_MESSAGE = "Board not found with ID: ";

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
    private

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
        when(mockUserRepository.findById(NON_EXIST_USER))
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
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(NON_EXIST_BOARD_ID, USER_ID))
                .thenReturn(null);
    }
    void setupTestTarget(){
        boardService = new BoardService(mockBoardRepository, mockUserRepository, mockUserBoardRepository, mockUserService, mockLocalizationService2);
    }

    @DisplayName("Attempt to create a board with an existing name should throw an exception")
    @Test
    void createBoardWithExistingNameShouldThrowException() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest(EXIT_BOARD_NAME, OWNER_ID, null);
        //when & then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> boardService.createBoard(request));
        assertEquals("Board already exists with name: " + EXIT_BOARD_NAME, exception.getMessage());
    }

    @DisplayName("Attempt to create a board with a non-existing owner should throw an exception")
    @Test
    void createBoardWithNonExistingOwnerShouldThrowException() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest(BOARD_NAME, NON_EXIST_USER, null);
        //when & then
        try{
            boardService.createBoard(request);
        }catch(IllegalArgumentException e){
            assertEquals("Owner not found", e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }
    @Test
    void createBoardWithUserBoardExitNull() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest("New Board", OWNER_ID, null);

        //when
        when(mockBoardRepository.findBoardsByName("New Board")).thenReturn(Optional.empty());
        when(mockUserRepository.findById(OWNER_ID)).thenReturn(Optional.of(testUser));
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(anyLong(), eq(OWNER_ID)))
                .thenReturn(null);
        //then
        final BoardDto dto = boardService.createBoard(request);
        assertNotNull(dto);
        assertEquals("New Board", dto.boardName());
    }

    @DisplayName("As user, I want to create a board with valid data, so that I can start collaborating.")
    @Test
    void createBoard() {
        //given
        final BoardCreatingRequest request = new BoardCreatingRequest(BOARD_NAME, OWNER_ID, null);
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

    @DisplayName("As user, I want to retrieve all boards with localization message.")
    @Test
    void getAllBoardsWithLocalizationMessage() {
        //when
        when(mockUserService.getLocale(testUser)).thenReturn("en");
        when(mockLocalizationService2.getMessage("messageOfTheDay", "en")).thenReturn("Message of the day:");
        when(mockLocalizationService2.getMessage("welcome", "en")).thenReturn("Welcome!");
        //then
        final List<BoardDto> boards = boardService.getAllBoards(testUser);

        assertNotNull(boards);
        assertEquals(1, boards.size());
        assertEquals("Message of the day:", boards.get(0).motdLabel());
        assertEquals("Welcome!", boards.get(0).customMessage());
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
            boardService.getBoardById(notFoundId, testUser);
        } catch (IllegalArgumentException e) {
            assertEquals(BOARD_NOT_FOUND_MESSAGE + notFoundId, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As user, I want to retrieve a board by its ID, so that I can view or edit it.")
    @Test
    void getBoardById() {
        //given
        final Long boardId = BOARD_ID;
        //when
        final BoardDto result = boardService.getBoardById(boardId, testUser);
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
            boardService.addUserToBoard(notFoundId, userId,testUser );
        } catch (IllegalArgumentException e) {
            assertEquals(BOARD_NOT_FOUND_MESSAGE + notFoundId, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As an admin, I want to get error when adding a user to a board with non-existing user ID")
    @Test
    void addUserToBoardWithNotExistingUserIdShouldThrowException() {
        //given
        final Long boardId = BOARD_ID;
        final Long notFoundId = NON_EXIST_USER;
        //When & Then
        try {
            boardService.addUserToBoard(boardId, notFoundId, testUser);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
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
            boardService.addUserToBoard(boardId, userId, testUser);
        } catch (IllegalArgumentException e) {
            assertEquals("User with ID: " + userId + " already exists in the board", e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As an admin, I want to add a new user to a board")
    @Test
    void addUserToBoardByNewUser() {
        //given
        final Long newUserId = 50L;
        final User newUser = new User();
        newUser.setId(newUserId);
        //when
        when(mockUserRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(BOARD_ID, newUserId))
                .thenReturn(null);
        //then
        final BoardDto dto = boardService.addUserToBoard(BOARD_ID, newUserId, testUser);
        assertNotNull(dto);
        verify(mockBoardRepository, times(1)).save(any(Board.class));
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
            boardService.addUserToBoard(boardId, userId, testUser);
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
        final Long userId = NON_EXIST_USER;
        //When & Then
        try {
            boardService.removeUserFromBoard(boardId, userId);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with ID: " + userId, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As an admin, I want to get error when removing a user from a board with non-existing board ID")
    @Test
    void removeUserFromBoardWithNotExistingBoardIdShouldThrowException() {
        //given
        final Long notFoundId = NON_EXIST_BOARD_ID;
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
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As an admin, I want to get error when removing a user from a board with null board in UserBoard")
    @Test
    void removeUserFromBoardUserBoardWithNullBoard() {
        //given
        final UserBoard userBoardWithNullBoard = new UserBoard();
        userBoardWithNullBoard.setUser(testUser);
        userBoardWithNullBoard.setBoard(null);
        //When & Then
        when(mockUserBoardRepository.findUserBoardByBoardIdAndUserId(BOARD_ID, USER_ID))
                .thenReturn(userBoardWithNullBoard);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                boardService.removeUserFromBoard(BOARD_ID, USER_ID));

        assertEquals("Board not found with ID: " + BOARD_ID, exception.getMessage());
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
        final BoardUpdateRequest request = new BoardUpdateRequest(boardName, numberOfStrokes, null);
        //When & Then
        try {
            boardService.updateBoard(notFoundId, request);
        } catch (IllegalArgumentException e) {
            assertEquals(BOARD_NOT_FOUND_MESSAGE + notFoundId, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As a user, I want to update a board's fields with only board name")
    @Test
    void updateBoardFieldsOnlyBoardName() {
        //given
        final BoardUpdateRequest request = new BoardUpdateRequest("NameOnly", null, null);
        //when & then
        boolean updated = boardService.updateBoardFields(testBoard, request);
        assertTrue(updated);
        assertEquals("NameOnly", testBoard.getName());
        assertEquals(NUMBER_OF_STROKES, testBoard.getNumberOfStrokes());
    }

    @DisplayName("As a user, I want to update a board's fields with only number of strokes")
    @Test
    void updateBoardFieldsOnlyNumberOfStrokes() {
        //given
        final BoardUpdateRequest request = new BoardUpdateRequest(null, 10, null);
        //when & then
        boolean updated = boardService.updateBoardFields(testBoard, request);
        assertTrue(updated);
        assertEquals(BOARD_NAME, testBoard.getName());
        assertEquals(10, testBoard.getNumberOfStrokes());
    }

    @DisplayName("As a user, I want to update a board's fields with no changes")
    @Test
    void updateBoardFieldsNoChanges() {
        //given
        final BoardUpdateRequest request = new BoardUpdateRequest(null, null, null);
        //when & then
        boolean updated = boardService.updateBoardFields(testBoard, request);
        assertFalse(updated);
        assertEquals(BOARD_NAME, testBoard.getName());
        assertEquals(NUMBER_OF_STROKES, testBoard.getNumberOfStrokes());
    }

    @Test
    void updateBoardWithNoFields() {
        // given
        final Long boardId = BOARD_ID;
        final BoardUpdateRequest request = new BoardUpdateRequest(null, null, null);

        // when
        final BoardDto result = boardService.updateBoard(boardId, request);

        // then
        assertNotNull(result);
        assertEquals(boardId, result.id());
        assertEquals(BOARD_NAME, result.boardName());
        assertEquals(NUMBER_OF_STROKES, result.numberOfStrokes());
    }

    @DisplayName("As a user, I want to update a board with valid data")
    @Test
    void updateBoard() {
        //given
        final Long boardId = BOARD_ID;
        final String boardName = UPDATED_BOARD_NAME;
        final Integer numberOfStrokes = NUMBER_OF_STROKES;
        final BoardUpdateRequest request = new BoardUpdateRequest(boardName, numberOfStrokes, null);
        //when
        final BoardDto result = boardService.updateBoard(boardId, request);
        //then
        assertNotNull(result);
        assertEquals(boardId, result.id());
        assertEquals(UPDATED_BOARD_NAME, result.boardName());
        assertEquals(NUMBER_OF_STROKES, result.numberOfStrokes());

    }
}