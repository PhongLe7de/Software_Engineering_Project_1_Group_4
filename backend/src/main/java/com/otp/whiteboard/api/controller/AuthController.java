package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.auth.AuthResponse;
import com.otp.whiteboard.dto.auth.LoginRequest;
import com.otp.whiteboard.dto.auth.RegisterRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.security.CustomUserDetailService;
import com.otp.whiteboard.security.CustomUserDetails;
import com.otp.whiteboard.security.JwtUtil;
import com.otp.whiteboard.service.AuthService;
import com.otp.whiteboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.otp.whiteboard.api.Endpoint.AUTH_INTERNAL_API;

@RestController
@RequestMapping(AUTH_INTERNAL_API)
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthService authService, AuthenticationManager authenticationManager, CustomUserDetailService customUserDetailService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailService = customUserDetailService;
        this.jwtUtil = jwtUtil;
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
    public ResponseEntity<AuthResponse> createUser(
            @RequestBody
            @NotNull (message = "request must not be null")
            @Valid final RegisterRequest request) {
        UserDto newUser = userService.createUser(request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email()
                        , request.password()
                )
        );
        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.ok(new AuthResponse(newUser, token));
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
    public ResponseEntity<Map<String, String>> login(
            @RequestBody
            @NotNull (message = "request must not be null")
            @Valid final LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email()
                        , request.password()
                )
        );
        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.ok(Map.of("token", token));
    }

}
