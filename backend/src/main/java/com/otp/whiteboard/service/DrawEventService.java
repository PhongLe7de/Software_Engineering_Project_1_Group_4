package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.model.Board;
import com.otp.whiteboard.model.Stroke;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.BoardRepository;
import com.otp.whiteboard.repository.StrokeRepository;
import com.otp.whiteboard.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DrawEventService {
    private static final Logger logger = LoggerFactory.getLogger(DrawEventService.class);

    private static final String DRAWING_CHANNEL_PREFIX = "drawing-session-";
    private static final String CURSOR_CHANNEL_PREFIX  = "cursor-session-";

    private static final String CURSOR_EVENTS_PREFIX   = "cursor-events";
    private static final String DRAWING_EVENTS_PREFIX  = "drawing-events-board-";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic drawTopic;
    private final ChannelTopic cursorTopic;
    private final StrokeRepository strokeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public DrawEventService(@NotNull @Valid final RedisTemplate<String, Object> redisTemplate,
                            @NotNull @Valid final ChannelTopic drawTopic,
                            @NotNull @Valid ChannelTopic cursorTopic,
                            @NotNull @Valid StrokeRepository strokeRepository,
                            @NotNull @Valid UserRepository userRepository,
                            @NotNull @Valid BoardRepository boardRepository) {
        this.redisTemplate = redisTemplate;
        this.drawTopic = drawTopic;
        this.cursorTopic = cursorTopic;
        this.strokeRepository = strokeRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    /** Broadcast + cache always; persist only if Board and User exist. */
    public DrawDto publishDrawEvent(DrawDto event) {
        logger.info("Publishing draw event: {}", event.id());
        // Realtime publish + cache in Redis
        final String drawChannelKey = DRAWING_CHANNEL_PREFIX + event.getBoardId();
        final String drawEventKey =  DRAWING_EVENTS_PREFIX + event.getBoardId();

        redisTemplate.convertAndSend(drawChannelKey, event);
        redisTemplate.opsForList().rightPush(drawEventKey, event);
        redisTemplate.expire(drawEventKey, java.time.Duration.ofHours(1));

        return saveStroke(event);
    }

    public void publishCursorEvent(CursorDto event) {
        logger.info("Publishing cursor event: {}", event.displayName());
        final String cursorChannelKey = CURSOR_CHANNEL_PREFIX + event.getDisplayName();
        final String cursorEventKey =  CURSOR_EVENTS_PREFIX + event.getDisplayName();

        redisTemplate.convertAndSend(cursorChannelKey, event);
        redisTemplate.opsForList().rightPush(cursorEventKey, event);
        redisTemplate.expire(cursorEventKey, java.time.Duration.ofHours(1));
    }

    /** History: prefer Redis; fall back to JPA if cache empty/expired. */
    @Nonnull
    public List<DrawDto> getBoardStrokes(Long boardId) {
        logger.debug("Getting board strokes: {}", boardId);
        final String drawEventKey = DRAWING_EVENTS_PREFIX + boardId;
        final List<Object> cached = redisTemplate.opsForList().range(drawEventKey, 0, -1);

        if (cached == null || cached.isEmpty()) {
            final List<Stroke> strokes = strokeRepository.findAllByBoardId(boardId);
            final List<DrawDto> result = strokes.stream().map(stroke -> new DrawDto(
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
            if(!result.isEmpty()) {
                redisTemplate.opsForList().rightPush(drawEventKey, result);
                redisTemplate.expire(drawEventKey, java.time.Duration.ofHours(1));
            }
            return result;
        }
        return cached.stream().map(e -> (DrawDto) e).toList();
    }

    @Nonnull
    private DrawDto saveStroke(DrawDto event) {
        logger.debug("Saving stroke for event: {}", event.id());
        try {
            final Board board = boardRepository.findById(event.getBoardId()).orElseThrow(
                    () -> new IllegalArgumentException("Board not found: " + event.getBoardId())
            );
            final User user = userRepository.findUserByDisplayName(event.getDisplayName()).orElseThrow(
                    () -> new IllegalArgumentException("User not found: " + event.getDisplayName())
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

            return event;
        } catch (Exception e) {
            logger.error("Error during removing user from board", e);
            throw e;
        }
    }

}
