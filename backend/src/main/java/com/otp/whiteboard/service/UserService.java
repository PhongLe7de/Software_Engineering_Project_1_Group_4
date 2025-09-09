package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.request.*;
import com.otp.whiteboard.dto.response.UserDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import com.otp.whiteboard.enums.Status;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user in the system.
     *
     * @param request The registration request containing user details.
     * @return The created user's details as a UserDto.
     */
    @Nonnull
    public UserDto createUser(@Valid final RegisterRequest request) {
        logger.debug("Creating user with email: {}", request.email());
        try {
            final Optional<User> existingUser = userRepository.findByEmail(request.email());
            if (existingUser.isPresent()) {
                logger.warn("Attempt to create user with existing email: {}", request.email());
                throw new IllegalArgumentException("User already exists with email: " + request.email());
            }

            User user = new User();
            user.setDisplayName(request.displayName());
            user.setEmail(request.email());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setStatus(Status.ACTIVE);

            final User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
            return new UserDto(savedUser);
        } catch (Exception error) {
            logger.error("Error during user creation: {}", error.getMessage());
            throw error;
        }
    }

    /**
     * Updates an existing user's details.
     *
     * @param id      The ID of the user to update.
     * @param request The user update request containing new details.
     */
    @Nonnull
    public UserDto updateUser(@NotNull @Positive final Long id, @Valid final UserUpdateRequest request) {
        logger.debug("Updating user with ID: {}", id);
        try {
            final User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

            updateUserFields(user, request);
            final User updatedUser = userRepository.save(user);
            logger.info("User updated successfully with ID: {}", updatedUser.getId());
            return new UserDto(updatedUser);
        } catch (Exception error) {
            logger.error("Error during user update: {}", error.getMessage());
            throw error;
        }
    }

    /**
     * Retrieves a user's profile information based on their display name.
     *
     * @param request The request containing the display name to search for.
     */
    @Nonnull
    public UserDto getUserProfileByDisplayName(@NotNull @Valid final UserByDisplayNameRequest request) {
        logger.debug("Search user with display name: {}", request.displayName());
        try {
            final User user = userRepository.findUserByDisplayName(request.displayName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with display name: " + request.displayName()));
            return new UserDto(user);
        } catch (Exception error) {
            logger.error("Error during user search: {}", error.getMessage());
            throw error;
        }

    }

    /**
     * Retrieves a list of all users in the system.
     *
     */
    @Nonnull
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto::new)
                .toList();
    }

    private void updateUserFields(User user, UserUpdateRequest request) {
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        if (request.photoUrl() != null) {
            user.setPhotoUrl(request.photoUrl());
        }
    }
}
