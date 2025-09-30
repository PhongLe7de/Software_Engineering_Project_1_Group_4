package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.model.Board;
import com.otp.whiteboard.model.Stroke;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.BoardRepository;
import com.otp.whiteboard.repository.StrokeRepository;
import com.otp.whiteboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DrawEventService {
    private static final Logger log = LoggerFactory.getLogger(DrawEventService.class);

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

    public DrawEventService(RedisTemplate<String, Object> redisTemplate,
                            ChannelTopic drawTopic,
                            ChannelTopic cursorTopic,
                            StrokeRepository strokeRepository,
                            UserRepository userRepository,
                            BoardRepository boardRepository) {
        this.redisTemplate = redisTemplate;
        this.drawTopic = drawTopic;
        this.cursorTopic = cursorTopic;
        this.strokeRepository = strokeRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    /** Broadcast + cache always; persist only if Board and User exist. */
    public void publishDrawEvent(DrawDto event) {
        // Realtime publish + cache in Redis
        String redisChannel = DRAWING_CHANNEL_PREFIX + event.getBoardId();
        redisTemplate.convertAndSend(redisChannel, event);
        redisTemplate.opsForList().rightPush(DRAWING_EVENTS_PREFIX + event.getBoardId(), event);
        redisTemplate.expire(DRAWING_EVENTS_PREFIX + event.getBoardId(), java.time.Duration.ofHours(1));

        // Best-effort DB persistence (no exceptions)
        var boardOpt = boardRepository.findById(event.getBoardId());
        if (boardOpt.isEmpty()) {
            log.warn("publishDrawEvent: board {} not found – skipping DB save (event id={})",
                    event.getBoardId(), event.getId());
            return;
        }

        var userOpt = userRepository.findUserByDisplayName(event.getDisplayName());
        if (userOpt.isEmpty()) {
            log.warn("publishDrawEvent: user '{}' not found – skipping DB save (event id={})",
                    event.getDisplayName(), event.getId());
            return;
        }

        Board board = boardOpt.get();
        User user = userOpt.get();

        Stroke stroke = new Stroke(
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

        // Optional: keep a counter on the board
        board.incrementStrokes();
        boardRepository.save(board);
    }

    public void publishCursorEvent(CursorDto event) {
        String cursorChannel = CURSOR_CHANNEL_PREFIX + event.getDisplayName();
        redisTemplate.convertAndSend(cursorChannel, event);
        redisTemplate.opsForList().rightPush(CURSOR_EVENTS_PREFIX + event.getDisplayName(), event);
        redisTemplate.expire(CURSOR_EVENTS_PREFIX + event.getDisplayName(), java.time.Duration.ofHours(1));
    }

    /** History: prefer Redis; fall back to JPA if cache empty/expired. */
    public List<DrawDto> getBoardStrokes(Long boardId) {
        String redisKey = DRAWING_EVENTS_PREFIX + boardId;
        List<Object> cached = redisTemplate.opsForList().range(redisKey, 0, -1);

        if (cached == null || cached.isEmpty()) {
            List<Stroke> strokes = strokeRepository.findAllByBoardId(boardId);
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
