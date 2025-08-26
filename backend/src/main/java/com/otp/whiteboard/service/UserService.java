package com.otp.whiteboard.service;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import com.otp.whiteboard.security.FirebaseAuthenticationContext;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public User registerUser(@NotBlank String email) {
        logger.info("Registering user with email: {}", email);

        // Extract Firebase UID from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = null;
        
        if (auth instanceof FirebaseAuthenticationContext) {
            FirebaseAuthenticationContext firebaseAuth = (FirebaseAuthenticationContext) auth;
            firebaseUid = firebaseAuth.getCredentials().getUid();
            logger.info("Extracted Firebase UID from authentication context: {}", firebaseUid);
        } else {
            throw new IllegalStateException("Firebase authentication required but not found in security context");
        }

        // Check if user already exists by email
        if (userRepository.existsByEmail(email)) {
            logger.warn("User with email {} already exists", email);
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Check if user already exists by Firebase UID
        if (userRepository.existsByUid(firebaseUid)) {
            logger.warn("User with Firebase UID {} already exists", firebaseUid);
            throw new IllegalArgumentException("User with this Firebase UID already exists");
        }

        final User user = new User(email);
        user.setUid(firebaseUid); // Set Firebase UID
        user.setStatus("ACTIVE"); // Set default status
        user.setCreatedAt(java.time.LocalDateTime.now()); // Set current timestamp

        final Map<String, List<String>> errors = validateUser(user);
        if (!errors.isEmpty()) {
            logger.error("Validation errors for user registration: {}", errors);
            throw new IllegalArgumentException("Validation errors for user registration");
        }

        userRepository.save(user);
        logger.info("User registered successfully with email: {} and Firebase UID: {}", email, firebaseUid);

        return userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalStateException("User not found after registration")
        );
    }

    private Map<String, List<String>> validateUser(User user) {
        Map<String, List<String>> errors = new HashMap<>();

        return errors;
    }
}
