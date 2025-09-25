package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.model.Board;
import com.otp.whiteboard.model.Stroke;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.BoardRepository;
import com.otp.whiteboard.repository.StrokeRepository;
import com.otp.whiteboard.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrawEventService {
    private final static String DRAWING_CHANNEL_PREFIX = "drawing-session-";
    private final static String CURSOR_CHANNEL_PREFIX = "cursor-session-";

    private final static String CURSOR_EVENTS_PREFIX = "cursor-events";
    private final static String DRAWING_EVENTS_PREFIX = "drawing-events-board-";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic drawTopic;
    private final ChannelTopic cursorTopic;
    private final StrokeRepository strokeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public DrawEventService(RedisTemplate <String, Object> redisTemplate, ChannelTopic drawTopic, ChannelTopic cursorTopic, StrokeRepository strokeRepository, UserRepository userRepository, BoardRepository boardRepository) {
        this.redisTemplate = redisTemplate;
        this.drawTopic = drawTopic;
        this.cursorTopic = cursorTopic;
        this.strokeRepository = strokeRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public void publishDrawEvent(DrawDto event) {
        String redisChannel = DRAWING_CHANNEL_PREFIX + event.getBoardId();
        redisTemplate.convertAndSend(redisChannel, event);
        redisTemplate.opsForList().rightPush(DRAWING_EVENTS_PREFIX + event.getBoardId(), event);
        redisTemplate.expire(DRAWING_EVENTS_PREFIX + event.getBoardId(), java.time.Duration.ofHours(1));

        Board board = boardRepository.findById(event.getBoardId()).orElseThrow(() -> new IllegalArgumentException("Board not found"));
        User user = userRepository.findUserByDisplayName(event.getDisplayName()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Stroke stroke = new Stroke(
                board,
                user,
                event.getBrushColor(),
                event.getBrushSize(),
                event.getType(),
                event.getTool(),
                event.getX(),
                event.getY(),
                java.time.LocalDateTime.now()
        );

        strokeRepository.save(stroke);
    }

    public void publishCursorEvent(CursorDto event) {
        String cursorChannel = CURSOR_CHANNEL_PREFIX + event.getDisplayName();
        redisTemplate.convertAndSend(cursorChannel, event);
        redisTemplate.opsForList().rightPush(CURSOR_EVENTS_PREFIX + event.getDisplayName(), event);
        redisTemplate.expire(CURSOR_EVENTS_PREFIX + event.getDisplayName(), java.time.Duration.ofHours(1));
    }

    public List<DrawDto> getBoardStrokes(Long boardId) {
        String redisKey = DRAWING_EVENTS_PREFIX + boardId;
        List<Object> cachedEvents = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (cachedEvents == null || cachedEvents.isEmpty()) {
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
        } else {
            return cachedEvents.stream().map(event -> (DrawDto) event).toList();
        }
    }
}
