package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.enums.DrawEventType;
import com.otp.whiteboard.enums.DrawingTool;
import com.otp.whiteboard.model.Board;
import com.otp.whiteboard.model.Stroke;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.BoardRepository;
import com.otp.whiteboard.repository.StrokeRepository;
import com.otp.whiteboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DrawEventServiceTest {
    private DrawEventService drawEventService;

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Test User";
    private static final String USER_PHOTO_URL = "http://example.com/photo.jpg";
    private static final Long BOARD_ID = 1L;
    private static final Long NON_EXISTENT_BOARD_ID = 999L;
    private static final String BOARD_NAME = "Test Board";
    private static final String STROKE_COLOR = "#000000";
    private static final long STROKE_THICKNESS = 5L;
    private static final double X_COORD = 100.0;
    private static final double Y_COORD = 150.0;

    private static final String DRAWING_CHANNEL_PREFIX = "drawing-session-";
    private static final String CURSOR_CHANNEL_PREFIX  = "cursor-session-";

    private static final String CURSOR_EVENTS_PREFIX   = "cursor-events";
    private static final String DRAWING_EVENTS_PREFIX  = "drawing-events-board-";
    @Mock
    RedisTemplate<String, Object> mockRedisTemplate;

    @Mock
    ListOperations<String, Object> mockListOperations;

    @Mock
    ChannelTopic drawTopic;

    @Mock
    ChannelTopic cursorTopic;

    @Mock
    StrokeRepository mockStrokeRepository;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    BoardRepository mockBoardRepository;

    private User testUser;
    private Board testBoard;
    private Stroke testStroke;
    private DrawDto testEvent;
    private CursorDto testCursor;

    @BeforeEach
    void init(){
        setupMockData();
        setupMocks();
        setupTestTarget();
    }

    void setupMockData(){
        testStroke = new Stroke();
        testStroke.setId(1L);
        testStroke.setColor(STROKE_COLOR);
        testStroke.setThickness(STROKE_THICKNESS);
        testStroke.setTool(DrawingTool.PEN);
        testStroke.setType(DrawEventType.DRAW);
        testStroke.setX_cord(X_COORD);
        testStroke.setY_cord(Y_COORD);

        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setDisplayName(USER_NAME);

        testBoard = new Board();
        testBoard.setId(BOARD_ID);
        testBoard.setName(BOARD_NAME);

        testStroke.setUser(testUser);
        testStroke.setBoard(testBoard);
        testStroke.setCreatedAt(LocalDateTime.now());
        testEvent = new DrawDto(
                "event1",
                BOARD_ID,
                USER_NAME,
                System.currentTimeMillis(),
                DrawEventType.DRAW,
                DrawingTool.PEN,
                X_COORD,
                Y_COORD,
                STROKE_THICKNESS,
                STROKE_COLOR,
                "stroke1"
        );

        testCursor = new CursorDto(
                USER_NAME,
                USER_PHOTO_URL,
                X_COORD,
                Y_COORD
        );
    }

    void setupMocks(){

        when(mockBoardRepository.findById(BOARD_ID)).thenReturn(Optional.of(testBoard));
        when(mockUserRepository.findUserByDisplayName(USER_NAME)).thenReturn(Optional.of(testUser));
        when(mockRedisTemplate.opsForList()).thenReturn(mockListOperations);

        when(mockListOperations.rightPush(eq(DRAWING_EVENTS_PREFIX + BOARD_ID), eq(testEvent)))
                .thenReturn(1L);
        when(mockListOperations.rightPush(eq(CURSOR_EVENTS_PREFIX + USER_PHOTO_URL), eq(testCursor)))
                .thenReturn(1L);

        when(mockBoardRepository.findById(NON_EXISTENT_BOARD_ID)).thenReturn(Optional.empty());
    }

    void setupTestTarget(){
        drawEventService = new DrawEventService(
                mockRedisTemplate,
                drawTopic,
                cursorTopic,
                mockStrokeRepository,
                mockUserRepository,
                mockBoardRepository
        );
    }

    @DisplayName("As user draws on board, draw event is published to Redis and saved to DB")
    @Test
    void publishDrawEvent() {
        // given
        DrawDto drawDto = testEvent;
        // when
        drawEventService.publishDrawEvent(drawDto);
        // then
        // Redis interactions
        verify(mockRedisTemplate).convertAndSend(
                eq(DRAWING_CHANNEL_PREFIX + BOARD_ID), eq(drawDto));
        verify(mockRedisTemplate.opsForList()).rightPush(
                eq(DRAWING_EVENTS_PREFIX + BOARD_ID), eq(drawDto));
        verify(mockRedisTemplate).expire(
                eq(DRAWING_EVENTS_PREFIX + BOARD_ID), any(Duration.class));

        // DB persistence
        verify(mockStrokeRepository).save(any(Stroke.class));
        verify(mockBoardRepository).save(testBoard);
    }

    @DisplayName("As user draws on board, but board not found, event is published to Redis but NOT saved to DB")
    @Test
    void publishDrawEvent_noBoard() {
        // given
        DrawDto testEvent2 = new DrawDto(
                "event2",
                NON_EXISTENT_BOARD_ID,
                USER_NAME,
                System.currentTimeMillis(),
                DrawEventType.DRAW,
                DrawingTool.PEN,
                X_COORD,
                Y_COORD,
                STROKE_THICKNESS,
                STROKE_COLOR,
                "stroke2"
        );
        when(mockListOperations.rightPush(eq(DRAWING_EVENTS_PREFIX + NON_EXISTENT_BOARD_ID), eq(testEvent2)))
                .thenReturn(1L);
        // when & then
        try{
            drawEventService.publishDrawEvent(testEvent2);
        } catch (Exception e){
            verify(mockRedisTemplate).convertAndSend(
                    eq(DRAWING_CHANNEL_PREFIX + NON_EXISTENT_BOARD_ID), eq(testEvent2));
            verify(mockRedisTemplate.opsForList()).rightPush(
                    eq(DRAWING_EVENTS_PREFIX + NON_EXISTENT_BOARD_ID), eq(testEvent2));
            verify(mockRedisTemplate).expire(
                    eq(DRAWING_EVENTS_PREFIX + NON_EXISTENT_BOARD_ID), any(Duration.class));
            verify(mockStrokeRepository,  org.mockito.Mockito.never()).save(any(Stroke.class));
            verify(mockBoardRepository, org.mockito.Mockito.never()).save(any(Board.class));
            assertEquals("Board not found: " + testEvent2.getBoardId(), e.getMessage());
            return;
        }
        fail("Expected exception was not thrown");
    }

    @DisplayName("As user draws on board, but user not found, event is published to Redis but NOT saved to DB")
    @Test
    void publishDrawEvent_noUser() {
        // given
        String nonExistentUser = "NonExistentUser";
        DrawDto testEvent2 = new DrawDto(
                "event2",
                BOARD_ID,
                nonExistentUser,
                System.currentTimeMillis(),
                DrawEventType.DRAW,
                DrawingTool.PEN,
                X_COORD,
                Y_COORD,
                STROKE_THICKNESS,
                STROKE_COLOR,
                "stroke2"
        );
        when(mockUserRepository.findUserByDisplayName(nonExistentUser)).thenReturn(Optional.empty());
        when(mockListOperations.rightPush(eq(DRAWING_EVENTS_PREFIX + BOARD_ID), eq(testEvent2)))
                .thenReturn(1L);
        // when & then
        try{
            drawEventService.publishDrawEvent(testEvent2);
        } catch (Exception e){
            verify(mockRedisTemplate).convertAndSend(
                    eq(DRAWING_CHANNEL_PREFIX + BOARD_ID), eq(testEvent2));
            verify(mockRedisTemplate.opsForList()).rightPush(
                    eq(DRAWING_EVENTS_PREFIX + BOARD_ID), eq(testEvent2));
            verify(mockRedisTemplate).expire(
                    eq(DRAWING_EVENTS_PREFIX + BOARD_ID), any(Duration.class));
            verify(mockStrokeRepository,  org.mockito.Mockito.never()).save(any(Stroke.class));
            verify(mockBoardRepository, org.mockito.Mockito.never()).save(any(Board.class));
            assertEquals("User not found: " + testEvent2.getDisplayName(), e.getMessage());
            return;
        }
        fail("Expected exception was not thrown");
    }

    @DisplayName("As user moves cursor on board, cursor event is published to Redis")
    @Test
    void publishCursorEvent() {
        // given
        CursorDto cursorEvent = testCursor;
        // when
        drawEventService.publishCursorEvent(cursorEvent);
        // then
        verify(mockRedisTemplate).convertAndSend(
                eq(CURSOR_CHANNEL_PREFIX + USER_NAME), eq(cursorEvent));
        verify(mockRedisTemplate.opsForList()).rightPush(
                eq(CURSOR_EVENTS_PREFIX + USER_NAME), eq(cursorEvent));
        verify(mockRedisTemplate).expire(
                eq(CURSOR_EVENTS_PREFIX + USER_NAME), any(Duration.class));
    }

    @DisplayName("Get all strokes for a board from DB")
    @Test
    void getBoardStrokes() {
        // given
        when(mockStrokeRepository.findAllByBoardId(BOARD_ID))
                .thenReturn(java.util.List.of(testStroke));
        // when
        var strokes = drawEventService.getBoardStrokes(BOARD_ID);
        // then
        verify(mockRedisTemplate.opsForList()).range(
                eq(DRAWING_EVENTS_PREFIX + BOARD_ID), eq(0L), eq(-1L));
        verify(mockStrokeRepository).findAllByBoardId(BOARD_ID);
        assertNotNull(strokes);
        assertEquals(1, strokes.size());
        DrawDto strokeDto = strokes.get(0);
        assertEquals(testEvent.getBoardId(), strokeDto.getBoardId());
        assertEquals(testEvent.getDisplayName(), strokeDto.getDisplayName());
        assertEquals(testEvent.getBrushColor(), strokeDto.getBrushColor());
        assertEquals(testEvent.getBrushSize(), strokeDto.getBrushSize());
        assertEquals(testEvent.getTool(), strokeDto.getTool());
        assertEquals(testEvent.getType(), strokeDto.getType());
        assertEquals(testEvent.getX(), strokeDto.getX());
        assertEquals(testEvent.getY(), strokeDto.getY());
    }

}