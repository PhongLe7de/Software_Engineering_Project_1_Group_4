package com.otp.whiteboard.service;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.JwtUtil;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Authenticates a user and generates a JWT token upon successful authentication.
     *
     * @param email    The user's email.
     * @param password The user's password.

     */
    @Nonnull
    public String login(String email, String password) {
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("Invalid password for user: {}", email);
                throw new IllegalArgumentException("Invalid email or password");
            }

            return jwtUtil.generateToken(user.getEmail());

        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", email);
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
    /**
     * Registers a new user with the provided details.
     *
     * @param email       The user's email.
     * @param password    The user's password.
     * @param displayName The user's display name.
     * @param photoUrl    The URL of the user's profile photo.
     */
    @Nonnull
    public User register(String email, String password, String displayName, String photoUrl) {
        try {
            return userService.registerUser(email, password, displayName, photoUrl);
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", email, e);
            throw e;
        }
    }
}