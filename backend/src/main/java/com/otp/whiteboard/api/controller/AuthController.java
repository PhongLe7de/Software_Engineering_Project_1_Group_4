package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.request.auth.LoginRequest;
import com.otp.whiteboard.dto.request.auth.RegisterRequest;
import com.otp.whiteboard.dto.response.user.UserDto;
import com.otp.whiteboard.service.AuthService;
import com.otp.whiteboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.otp.whiteboard.api.Endpoint.AUTH_INTERNAL_API;

@RestController
@RequestMapping(AUTH_INTERNAL_API)
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = """
                    This endpoint allows for the registration of a new user.
                    It accepts a JSON payload containing the user's display name, email, and password.
                    Upon successful registration, it returns the details of the newly created user.
                    The password is securely hashed before storage to ensure user security.
                    """
    )
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<UserDto> createUser(
            @RequestBody
            @NotNull (message = "request must not be null")
            @Valid final RegisterRequest request) {
        UserDto  newUser= userService.createUser(request);
        return ResponseEntity.ok(newUser);
    }

    @Operation(
            summary = "User login",
            description = """
                    This endpoint handles user login by validating the provided email and password.
                    It accepts a JSON payload containing the user's email and password.
                    The password is securely compared using hashing to ensure user security.
                    """
    )
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<UserDto> login(
            @RequestBody
            @NotNull (message = "request must not be null")
            @Valid final LoginRequest request) {
        UserDto user = authService.login(request);
        return ResponseEntity.ok(user);
    }

}
