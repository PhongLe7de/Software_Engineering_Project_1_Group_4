package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.drawing.DrawDto;
import com.otp.whiteboard.service.DrawEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.otp.whiteboard.api.Endpoint.DRAW_INTERNAL_API;

@RestController
@RequestMapping(DRAW_INTERNAL_API)
@SecurityRequirement(name = "Bearer Authentication")
public class DrawTestController {
    private final DrawEventService drawEventService;

    public DrawTestController(DrawEventService drawEventService) {
        this.drawEventService = drawEventService;
    }

    @PostMapping()
    public DrawDto testDraw(@RequestBody DrawDto event) {
        return drawEventService.publishDrawEvent(event);
    }
}
