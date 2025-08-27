package com.otp.whiteboard.controller;

import com.otp.whiteboard.dto.request.LoginRequest;
import com.otp.whiteboard.dto.request.RegisterRequest;
import com.otp.whiteboard.dto.response.LoginResponse;
import com.otp.whiteboard.dto.response.RegisterResponse;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.service.AuthService;
import com.otp.whiteboard.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for email: {}", request.getEmail());
            
            String token = authService.login(request.getEmail(), request.getPassword());
            User user = userService.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
            
            LoginResponse response = new LoginResponse(token, user);
            logger.info("Login successful for email: {}", request.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Registration attempt for email: {}", request.getEmail());
            
            User user = authService.register(
                request.getEmail(), 
                request.getPassword(), 
                request.getDisplayName(), 
                request.getPhotoUrl()
            );
            
            RegisterResponse response = new RegisterResponse("User registered successfully", user);
            logger.info("Registration successful for email: {}", request.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}