package com.otp.whiteboard.service;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public String login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
            
            logger.info("User authenticated successfully: {}", email);
            return jwtUtil.generateToken(email);
            
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", email);
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public User register(String email, String password, String displayName, String photoUrl) {
        try {
            return userService.registerUser(email, password, displayName, photoUrl);
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", email, e);
            throw e;
        }
    }
}