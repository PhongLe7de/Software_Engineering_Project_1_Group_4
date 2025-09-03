package com.otp.whiteboard.api;

import com.otp.whiteboard.dto.request.LoginRequest;
import com.otp.whiteboard.dto.request.RegisterRequest;
import com.otp.whiteboard.dto.response.LoginDto;
import com.otp.whiteboard.dto.response.RegisterDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.service.AuthService;
import com.otp.whiteboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController( AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDto> login(@Valid @RequestBody LoginRequest request) {
            String token = authService.login(request.getEmail(), request.getPassword());
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
            return ResponseEntity.ok(new LoginDto(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterDto> register(@Valid @RequestBody RegisterRequest request) {
            User user = authService.register(
                request.getEmail(), 
                request.getPassword(), 
                request.getDisplayName(), 
                request.getPhotoUrl()
            );
            return ResponseEntity.ok(new RegisterDto(user));
    }
}