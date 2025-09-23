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

@Service
public class DrawEventService {
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
        String redisChannel = "drawing-session-" + event.getBoardId();
        redisTemplate.convertAndSend(redisChannel, event);
        redisTemplate.opsForList().rightPush("drawing-events-board-" + event.getBoardId(), event);
        redisTemplate.expire("drawing-events-board-" + event.getBoardId(), java.time.Duration.ofHours(1));

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
        String cursorChannel = "cursor-session-" + event.getDisplayName();
        redisTemplate.convertAndSend(cursorChannel, event);
        redisTemplate.opsForList().rightPush("cursor-events" + event.getDisplayName(), event);
        redisTemplate.expire("cursor-events" + event.getDisplayName(), java.time.Duration.ofHours(1));
    }
}
