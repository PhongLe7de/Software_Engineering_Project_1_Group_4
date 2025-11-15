package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.model.Board;
import com.otp.whiteboard.model.Stroke;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.BoardRepository;
import com.otp.whiteboard.repository.StrokeRepository;
import com.otp.whiteboard.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DrawEventService {
    private static final Logger log = LoggerFactory.getLogger(DrawEventService.class);

    private static final String DRAWING_CHANNEL_PREFIX = "drawing-session-";
    private static final String CURSOR_CHANNEL_PREFIX  = "cursor-session-";

    private static final String CURSOR_EVENTS_PREFIX   = "cursor-events";
    private static final String DRAWING_EVENTS_PREFIX  = "drawing-events-board-";

    private final RedisTemplate<String, Object> redisTemplate;
    private final StrokeRepository strokeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public DrawEventService(final RedisTemplate<String, Object> redisTemplate,
                            final StrokeRepository strokeRepository,
                            final UserRepository userRepository,
                            final BoardRepository boardRepository) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "redisTemplate must not be null");
        this.strokeRepository = Objects.requireNonNull(strokeRepository, "strokeRepository must not be null");
        this.boardRepository = Objects.requireNonNull(boardRepository, "boardRepository must not be null");
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
    }

    /**
     * Publish a drawing event to Redis and save it to the database.
     */
    public void publishDrawEvent(@NotNull @Valid final DrawDto event) {
        final String redisChannel = DRAWING_CHANNEL_PREFIX + event.getBoardId();

        redisTemplate.convertAndSend(redisChannel, event);
        redisTemplate.opsForList().rightPush(DRAWING_EVENTS_PREFIX + event.getBoardId(), event);
        redisTemplate.expire(DRAWING_EVENTS_PREFIX + event.getBoardId(), java.time.Duration.ofHours(1));

        try {
            final Board board = boardRepository.findById(event.getBoardId()).orElseThrow(
                    () -> {
                        log.warn("publishDrawEvent: board {} not found – skipping DB save (event id={})",
                                event.getBoardId(), event.getId());
                        return new IllegalArgumentException("Board not found: " + event.getBoardId());
                    }
            );

            final User user = userRepository.findUserByDisplayName(event.getDisplayName()).orElseThrow(
                    () -> {
                        log.warn("publishDrawEvent: user {} not found – skipping DB save (event id={})",
                                event.getDisplayName(), event.getId());
                        return new IllegalArgumentException("User not found: " + event.getDisplayName());
                    }
            );

            final Stroke stroke = new Stroke(
                    board,
                    user,
                    event.getBrushColor(),
                    event.getBrushSize(),
                    event.getType(),
                    event.getTool(),
                    event.getX(),
                    event.getY(),
                    LocalDateTime.now()
            );

            strokeRepository.save(stroke);

            board.incrementStrokes();
            boardRepository.save(board);
        } catch (Exception e) {
            log.error("Error during removing user from board", e);
            throw e;
        }

    }

    /**
     * Publish a cursor event to Redis.
     */
    public void publishCursorEvent(@NotNull @Valid final CursorDto event) {
        final String cursorChannel = CURSOR_CHANNEL_PREFIX + event.getDisplayName();

        redisTemplate.convertAndSend(cursorChannel, event);
        redisTemplate.opsForList().rightPush(CURSOR_EVENTS_PREFIX + event.getDisplayName(), event);
        redisTemplate.expire(CURSOR_EVENTS_PREFIX + event.getDisplayName(), java.time.Duration.ofHours(1));
    }

    /**
     * Retrieve all drawing strokes for a given board.
     */
    @NonNull
    public List<DrawDto> getBoardStrokes(@NotNull final Long boardId) {
        final String redisKey = DRAWING_EVENTS_PREFIX + boardId;
        final List<Object> cached = redisTemplate.opsForList().range(redisKey, 0, -1);

        if (cached == null || cached.isEmpty()) {
            final List<Stroke> strokes = strokeRepository.findAllByBoardId(boardId);
            return strokes.stream().map(stroke -> new DrawDto(
                    stroke.getId().toString(),
                    stroke.getBoard().getId(),
                    stroke.getUser().getDisplayName(),
                    stroke.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    stroke.getType(),
                    stroke.getTool(),
                    stroke.getX_cord(),
                    stroke.getY_cord(),
                    stroke.getThickness(),
                    stroke.getColor(),
                    stroke.getId().toString()
            )).toList();
        }
        return cached.stream().map(e -> (DrawDto) e).toList();
    }
}
