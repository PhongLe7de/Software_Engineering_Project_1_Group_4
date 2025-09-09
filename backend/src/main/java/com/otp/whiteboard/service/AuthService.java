package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.auth.LoginRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Nonnull
    public UserDto login(@Valid final LoginRequest request) {
        logger.debug("Attempting login for email: {}", request.email());
        try{
            final User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

            if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                logger.warn("Failed login attempt for email: {}", request.email());
                throw new IllegalArgumentException("Invalid email or password");
            }

            return new UserDto(user);
        }catch (IllegalArgumentException e){
            logger.error("Error during login: {}", e.getMessage());
            throw e;
        }

    }
}
