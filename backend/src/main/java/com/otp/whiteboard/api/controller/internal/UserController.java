package com.otp.whiteboard.api.controller.internal;

import com.otp.whiteboard.dto.request.UserRegisterRequest;
import com.otp.whiteboard.dto.request.response.UserDto;
import com.otp.whiteboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.otp.whiteboard.api.Endpoint.USER_INTERNAL_API;

@RestController
@RequestMapping(USER_INTERNAL_API)
@Validated
@Tag(name = "User Management", description = "Protected user management operations - requires Firebase authentication")
@SecurityRequirement(name = "Firebase Bearer Authentication")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Register a new user in the system with email and password. Requires Firebase authentication."
    )
    @ResponseBody
    public UserDto registerUser(@RequestBody @NotNull @Valid UserRegisterRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }
        return new UserDto(userService.registerUser(request.getEmail(), request.getPassword()));
    }

}
