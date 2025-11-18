package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.dto.drawing.HistoryRequest;
import com.otp.whiteboard.service.DrawEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static com.otp.whiteboard.api.Endpoint.CURSOR_WEBSOCKET;
import static com.otp.whiteboard.api.Endpoint.DRAW_WEBSOCKET;
import static com.otp.whiteboard.api.Endpoint.HISTORY_WEBSOCKET;

@Controller
public class DrawController {

    private static final Logger log = LoggerFactory.getLogger(DrawController.class);
    private final DrawEventService drawEventService;

    public DrawController(DrawEventService drawEventService) {
        this.drawEventService = drawEventService;
    }

    @MessageMapping(DRAW_WEBSOCKET)
    @SendTo("/topic/draw")
    public DrawDto onDraw(@Payload DrawDto event) {
        drawEventService.publishDrawEvent(event);
        return event;
    }

    @MessageMapping(CURSOR_WEBSOCKET)
    @SendTo("/topic/cursor")
    public CursorDto onCursor(@Payload CursorDto cursor) {
        drawEventService.publishCursorEvent(cursor);
        return cursor;
    }

    @MessageMapping(HISTORY_WEBSOCKET)
    @SendToUser("/queue/history")
    public List<DrawDto> history(@Payload HistoryRequest req,
                                 @Header("simpSessionId") String sessionId,
                                 Principal principal) {
        try {
            log.info("History request: session={}, user={}, boardId={}, limit={}",
                    sessionId, principal != null ? principal.getName() : "anon", req.boardId(), req.limit());
            return drawEventService.getBoardStrokes(req.boardId());
        } catch (RuntimeException e) {
            log.error("History retrieval failed");
            return Collections.emptyList();
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Exception e) {
        log.error("WebSocket error: ", e);
        return e.getMessage();
    }
}
