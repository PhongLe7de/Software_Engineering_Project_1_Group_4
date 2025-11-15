package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.auth.LoginRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthService(final UserRepository userRepository,final PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder must not be null");
    }

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param request The login request containing email and password.
     * @return The authenticated user's details as a UserDto.
     * @throws IllegalArgumentException if authentication fails.
     */
    @Nonnull
    public UserDto login(@NotNull @Valid final LoginRequest request) {
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
