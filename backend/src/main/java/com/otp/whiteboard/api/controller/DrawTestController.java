package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.service.DrawEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.otp.whiteboard.api.Endpoint.DRAW_INTERNAL_API;
import static com.otp.whiteboard.api.Endpoint.USER_INTERNAL_API;

@RestController
@RequestMapping(DRAW_INTERNAL_API)
@SecurityRequirement(name = "Bearer Authentication")
public class DrawTestController {
    private final DrawEventService drawEventService;
    private static final Logger log = LoggerFactory.getLogger(DrawController.class);

    public DrawTestController(DrawEventService drawEventService) {
        this.drawEventService = drawEventService;
    }

    @PostMapping()
    public DrawDto testDraw(@RequestBody DrawDto event) {
        log.debug("Received draw event: {}", event);
        drawEventService.publishDrawEvent(event);
        return event;
    }
}
