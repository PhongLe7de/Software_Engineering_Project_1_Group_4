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
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BoardService {
    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserBoardRepository userBoardRepository;

    private final UserService userService;
    private final LocalizationService localizationService;

    public BoardService(final BoardRepository boardRepository,final UserRepository userRepository,final UserBoardRepository userBoardRepository,final UserService userService,final LocalizationService localizationService) {
        this.boardRepository = Objects.requireNonNull(boardRepository, "boardRepository must not be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.userBoardRepository = Objects.requireNonNull(userBoardRepository, "userBoardRepository must not be null");
        this.localizationService = Objects.requireNonNull(localizationService, "localizationService must not be null");
        this.userService = Objects.requireNonNull(userService, "userService must not be null");
    }

    /**
     * Creates a new board based on the provided request.
     *
     * @param request the board creation request containing board details.
     * @return the created board's details as a BoardDto.
     */
    @Nonnull
    public BoardDto createBoard(@NotNull @Valid final BoardCreatingRequest request) {
        logger.debug("Creating Board with name: {}", request.boardName());
        try {
            final Optional<Board> exitingBoard = boardRepository.findBoardsByName(request.boardName());
            if (exitingBoard.isPresent()) {
                logger.warn("Attempt to create board with existing name: {}", request.boardName());
                throw new IllegalArgumentException("Board already exists with name: " + request.boardName());
            }
            final Board newBoard = new Board();
            newBoard.setName(request.boardName());
            newBoard.setOwnerId(request.ownerId());

            final User owner = userRepository.findById(request.ownerId()).orElseThrow(
                    () -> new IllegalArgumentException("Owner not found")
            );
            newBoard.addUser(owner);
            boardRepository.save(newBoard);

            final UserBoard userBoardExit = userBoardRepository.findUserBoardByBoardIdAndUserId(newBoard.getId(), owner.getId());
            userBoardExit.setRole(Role.ADMIN);
            userBoardRepository.save(userBoardExit);

            logger.info("Board created successfully with ID: {} and name: {}", newBoard.getId(), newBoard.getName());
            return new BoardDto(newBoard);
        } catch (Exception error) {
            logger.error("Error during board creation: {}", error.getMessage());
            throw error;
        }
    }

    /**
     * Retrieves all boards.
     *
     * @return a list of all boards as BoardDto objects.
     */
    @Nonnull
    public List<BoardDto> getAllBoards(@NotNull @Valid final User user) {
        logger.debug("Fetching all boards");
        try {
            final String userLocale = userService.getLocale(user);
            final String welcomeMessage = localizationService.getMessage("welcome", userLocale);
            final List<Board> boards = boardRepository.findAll();
            return boards.stream().map(board -> new BoardDto(board).withMessage(welcomeMessage)).toList();
        } catch (Exception error) {
            logger.error("Error during fetching all boards: {}", error.getMessage());
            throw error;
        }
    }

    /**
     * Retrieves a board by its ID.
     *
     * @param boardId the ID of the board to be retrieved.
     * @return the board's details as a BoardDto.
     * @throws IllegalArgumentException if the board is not found.
     */
    @Nonnull
    public BoardDto getBoardById(@NotNull final Long boardId, @NotNull @Valid final User user) {
        try {
            final String userLocale = userService.getLocale(user);
            final String welcomeMessage = localizationService.getMessage("welcome", userLocale);
            final Optional<Board> optionalBoard = boardRepository.findById(boardId);
            if (optionalBoard.isEmpty()) {
                throw new IllegalArgumentException("Board not found with ID: " + boardId);
            }
            final Board board = optionalBoard.get();

            return new BoardDto(board).withMessage(welcomeMessage);
        } catch (Exception error) {
            logger.error("Error during fetching board by id: {}", error.getMessage());
            throw error;
        }
    }

    /**
     * Adds a user to a board.
     *
     * @param boardId the ID of the board.
     * @param userId  the ID of the user to be added.
     * @return the updated board's details as a BoardDto.
     * @throws IllegalArgumentException if the board or user is not found, or if the user is already in the board.
     */
    @Nonnull
    public BoardDto addUserToBoard(@NotNull final Long boardId, @NotNull final Long userId) {
        try {
            final Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));

            final User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException("User not found with ID: " + userId)
            );

            final UserBoard isUserExitsInBoard = userBoardRepository.findUserBoardByBoardIdAndUserId(boardId, userId);
            if (isUserExitsInBoard != null) {
                throw new IllegalArgumentException("User with ID: " + userId + " already exists in the board");
            }

            board.addUser(user);
            boardRepository.save(board);

            return new BoardDto(board);
        } catch (Exception error) {
            logger.error("Error during adding user to board", error);
            throw error;
        }
    }

    /**
     * Removes a user from a board.
     *
     * @param boardId the ID of the board.
     * @param userId  the ID of the user to be removed.
     * @return the updated board's details as a BoardDto.
     * @throws IllegalArgumentException if the board or user is not found, or if the user is not associated with the board.
     */
    @Nonnull
    public BoardDto removeUserFromBoard(@NotNull final Long boardId, @NotNull final Long userId) {
        try {
            final User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException("User not found with ID: " + userId)
            );

            final UserBoard userBoard = userBoardRepository.findUserBoardByBoardIdAndUserId(boardId, userId);
            if (userBoard == null) {
                throw new IllegalArgumentException("User with ID: " + userId + " is not associated with board ID: " + boardId);
            }
            final Board board = userBoard.getBoard();

            if (board == null) {
                throw new IllegalArgumentException("Board not found with ID: " + boardId);
            }

            board.removeUser(user);
            boardRepository.save(board);

            userBoardRepository.delete(userBoard);

            return new BoardDto(board);
        } catch (Exception error) {
            logger.error("Error during removing user from board", error);
            throw error;
        }
    }

    /**
     * Updates an existing board's details.
     *
     * @param boardId the ID of the board to be updated.
     * @param request the board update request containing new details.
     */
    @Nonnull
    public BoardDto updateBoard(@NotNull final Long boardId, @NotNull @Valid final BoardUpdateRequest request) {
        logger.debug("Updating Board with ID: {}", boardId);
        try {
            final Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + boardId));
            final boolean isUpdated = updateBoardFields(board, request);
            if (!isUpdated) {
                logger.info("No fields to update for board with ID: {}", boardId);
                return new BoardDto(board);
            }
            boardRepository.save(board);
            logger.info("Board updated successfully with ID: {}", board.getId());
            return new BoardDto(board);
        } catch (Exception error) {
            logger.error("Error during board update: {}", error.getMessage());
            throw error;
        }
    }

    /**
     * Updates the fields of a board based on the provided update request.
     *
     * @param board   the board to be updated.
     * @param request the board update request containing new details.
     */
    public boolean updateBoardFields(@NotNull final Board board, @NotNull final BoardUpdateRequest request) {
        boolean result = false;
        if (request.boardName() != null) {
            board.setName(request.boardName());
            result = true;
        }
        if (request.numberOfStrokes() != null) {
            board.setNumberOfStrokes(request.numberOfStrokes());
            result = true;
        }
        return result;
    }
}
