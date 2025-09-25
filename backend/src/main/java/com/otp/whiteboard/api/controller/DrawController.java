package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.dto.drawing.CursorDto;
import com.otp.whiteboard.service.DrawEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import static com.otp.whiteboard.api.Endpoint.CURSOR_WEBSOCKET;
import static com.otp.whiteboard.api.Endpoint.DRAW_WEBSOCKET;

@Controller
public class DrawController {
    private final DrawEventService drawEventService;
    private static final Logger log = LoggerFactory.getLogger(DrawController.class);

    public DrawController(DrawEventService drawEventService) {
        this.drawEventService = drawEventService;
    }

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public DrawDto onDraw(@Payload DrawDto event) {
        log.debug("Received draw event: {}", event);
        drawEventService.publishDrawEvent(event);
        return event; // This needs to return draw event for it to work
    }
    // Client sends to /app/cursor -> all subscribers of /topic/cursor receive the CursorDto

    @MessageMapping("/cursor")
    @SendTo("/topic/cursor")
    public CursorDto onCursor(@Payload CursorDto cursor) {
        return cursor;
    }
}

