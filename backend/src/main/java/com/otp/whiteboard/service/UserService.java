package com.otp.whiteboard.service;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import com.otp.whiteboard.enums.Status;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Nonnull
    public User registerUser(@NotBlank String email, @NotBlank String password, 
                           String displayName, String photoUrl) {
        logger.info("Registering user with email: {}", email);

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            logger.warn("User with email {} already exists", email);
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create new user
        User user = new User(email, passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setPhotoUrl(photoUrl);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(java.time.LocalDateTime.now());

        userRepository.save(user);
        logger.info("User registered successfully with email: {}", email);

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public void updateProfile(String email, String displayName, String photoUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setDisplayName(displayName);
        user.setPhotoUrl(photoUrl);
        userRepository.save(user);
        
        logger.info("Profile updated for user: {}", email);
    }

    @Transactional
    public void updateStatus(String email, Status status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setStatus(status);
        userRepository.save(user);
        
        logger.info("Status updated to {} for user: {}", status, email);
    }
}
