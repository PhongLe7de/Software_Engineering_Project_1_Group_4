package com.otp.whiteboard.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DrawController {

    private static final Logger log = LoggerFactory.getLogger(DrawController.class);

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public String onDraw(@Payload String body) {
        log.debug("draw event: {}", body);
        return body;
    }

    @MessageMapping("/cursor")
    @SendTo("/topic/cursor")
    public String onCursor(@Payload String body) {
        log.debug("cursor event: {}", body);
        return body;
    }
}
