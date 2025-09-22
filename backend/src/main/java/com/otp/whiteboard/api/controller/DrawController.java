package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.dto.drawing.CursorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DrawController {

    private static final Logger log = LoggerFactory.getLogger(DrawController.class);

    // Client sends to /app/draw -> all subscribers of /topic/draw receive the DrawEventDto

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public DrawDto onDraw(@Payload DrawDto event) {
        log.debug("Received draw event: {}", event);
        return event;
    }

    // Client sends to /app/cursor -> all subscribers of /topic/cursor receive the CursorDto

    @MessageMapping("/cursor")
    @SendTo("/topic/cursor")
    public CursorDto onCursor(@Payload CursorDto cursor) {
        log.debug("Received cursor event: {}", cursor);
        return cursor;
    }
}
