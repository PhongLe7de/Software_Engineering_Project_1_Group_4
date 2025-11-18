package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.dto.drawing.HistoryRequest;
import com.otp.whiteboard.enums.DrawEventType;
import com.otp.whiteboard.enums.DrawingTool;
import com.otp.whiteboard.service.DrawEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DrawControllerTest {
    private DrawController drawController;

    private static final String SESSION_ID = "session-1";
    private static final String EXCEPTION_MESSAGE = "History retrieval failed";

    private static final String DRAW_ID = "123";
    private static final Long BOARD_ID = 1L;
    private static final String DISPLAY_NAME = "Phong";
    private static final long TIMESTAMP = 1000L;

    private static final DrawEventType EVENT_TYPE = DrawEventType.DRAW;
    private static final DrawingTool TOOL = DrawingTool.PEN;
    private static final double X = 120.5;
    private static final double Y = 80.3;
    private static final Long BRUSH_SIZE = 5L;
    private static final String BRUSH_COLOR = "#000000";
    private static final String STROKE_ID = "stroke-1";

    private static final String CURSOR_ID = "456";
    private static final String CURSOR_PHOTO = "https://example.com/photo.jpg";
    private static final double CURSOR_X = 150.0;
    private static final double CURSOR_Y = 90.0;

    private static final Long HISTORY_BOARD_ID = 1L;
    private static final int HISTORY_LIMIT = 100;

    private DrawDto drawDto;
    private CursorDto cursorDto;
    private HistoryRequest historyRequest;

    @Mock
    private DrawEventService drawEventService;

    @BeforeEach
    void init() {
        setupTestTarget();
        setupMockData();
    }

    void setupMockData() {
        drawDto = new DrawDto(
                DRAW_ID,
                BOARD_ID,
                DISPLAY_NAME,
                TIMESTAMP,
                EVENT_TYPE,
                TOOL,
                X,
                Y,
                BRUSH_SIZE,
                BRUSH_COLOR,
                STROKE_ID
        );

        cursorDto = new CursorDto(
                CURSOR_ID,
                CURSOR_PHOTO,
                CURSOR_X,
                CURSOR_Y
        );

        historyRequest = new HistoryRequest(HISTORY_BOARD_ID, HISTORY_LIMIT);
    }

    void setupTestTarget() {
        drawController = new DrawController(drawEventService);
    }

    @Test
    void onDraw() {
        final DrawDto result = drawController.onDraw(drawDto);
        assertEquals(drawDto, result);
    }

    @Test
    void onCursor() {
        final CursorDto result = drawController.onCursor(cursorDto);
        assertEquals(cursorDto, result);
    }

    @DisplayName("As a user, I want to request drawing history so that I can retrieve the exception message")
    @Test
    void historyShouldReturnExceptionMessage() {
        final HistoryRequest req = historyRequest;
        //when
        when(drawEventService.getBoardStrokes(BOARD_ID))
                .thenThrow(new RuntimeException(EXCEPTION_MESSAGE));
        //then
        final List<DrawDto> result = drawController.history(req, SESSION_ID, null);
        assertTrue(result.isEmpty());
    }

    @DisplayName("As a user, I want to request drawing history so that I can retrieve the draw dto list")
    @Test
    void historyShouldReturnDrawDtoList() {
        final HistoryRequest req = historyRequest;
        final Principal principal = Mockito.mock(Principal.class);
        //when
        when(drawEventService.getBoardStrokes(HISTORY_BOARD_ID))
                .thenReturn(List.of(drawDto));
        //then
        try{
            List<DrawDto> result = drawController.history(req, SESSION_ID, principal);
            assertEquals(List.of(drawDto), result);
        } catch (Exception e){
            fail("Exception should not be thrown");
        }
    }

    @DisplayName("As a user, I want to handle exceptions so that I can log the error message")
    @Test
    void handleException() {
        final RuntimeException exception = new RuntimeException(EXCEPTION_MESSAGE);
        //when
        final String result = drawController.handleException(exception);
        //then
        assertEquals(EXCEPTION_MESSAGE, result);
    }
}