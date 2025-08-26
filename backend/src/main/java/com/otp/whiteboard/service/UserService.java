package com.otp.whiteboard.service;


import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Nonnull
    public User registerUser(@NotBlank String email, @NotBlank String password) {
        logger.info("Registering user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            logger.warn("User with email {} already exists", email);
            throw new IllegalArgumentException("User with this email already exists");
        }

        final User user = new User(email, password);
        final Map<String, List<String>> errors = validateUser(user);
        if (!errors.isEmpty()) {
            logger.error("Validation errors for user registration: {}", errors);
            throw new IllegalArgumentException("Validation errors for user registration");
        }

        userRepository.save(user);
        logger.info("User registered successfully with email: {}", email);

        return userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("User not found after registration")
        );
    }

    private Map<String, List<String>> validateUser(User user) {
        Map<String, List<String>> errors = new HashMap<>();

        return errors;
    }
}
