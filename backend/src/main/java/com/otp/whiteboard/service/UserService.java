package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.auth.RegisterRequest;
import com.otp.whiteboard.dto.user.UserByDisplayNameRequest;
import com.otp.whiteboard.dto.user.UserUpdateRequest;
import com.otp.whiteboard.dto.user.UserDto;
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
import reactor.util.annotation.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(@NonNull @Valid UserRepository userRepository, @NotNull @Valid PasswordEncoder passwordEncoder) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder must not be null");
    }
    /**
     * Creates a new user in the system.
     *
     * @param request The registration request containing user details.
     * @return The created user's details as a UserDto.
     */
    @Nonnull
    public User createUser(@NotNull @Valid final RegisterRequest request) {
        logger.debug("Creating user with email: {}", request.email());
        try {
            final Optional<User> existingUser = userRepository.findByEmail(request.email());
            if (existingUser.isPresent()) {
                logger.warn("Attempt to create user with existing email: {}", request.email());
                throw new IllegalArgumentException("User already exists with email: " + request.email());
            }

            final User user = new User();
            user.setEmail(request.email());
            user.setPhotoUrl(request.photoUrl());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setDisplayName(request.displayName());
            user.setStatus(Status.ACTIVE);
            user.setLocale(request.locale() != null ? request.locale() : "en");

            final User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
            return savedUser;
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
    public UserDto updateUser(@NotNull @Positive final Long id, @NotNull @Valid final UserUpdateRequest request) {
        logger.debug("Updating user with ID: {}", id);
        try {
            final User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

            boolean isUpdated = updateUserFields(user, request);
            if (!isUpdated) {
                logger.info("No fields to update for user with ID: {}", id);
                return new UserDto(user);
            }
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

    /**
     * Retrieves a user's details based on their email address.
     *
     * @param email The email address of the user to retrieve.
     * @return The user's details as a UserDto.
     */
    @NonNull
    public User getUserByEmail(@NotNull final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    /**
     * Updates the fields of a user based on the provided update request.
     *
     * @param user    The user to update.
     * @param request The user update request containing new details.
     * @return true if any field was updated, false otherwise.
     */
    private boolean updateUserFields(@NotNull @Valid final User user,@NotNull @Valid final UserUpdateRequest request) {
        boolean isUpdated = false;
        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
            isUpdated = true;
        }
        if (request.status() != null) {
            user.setStatus(request.status());
            isUpdated = true;
        }
        if (request.photoUrl() != null) {
            user.setPhotoUrl(request.photoUrl());
            isUpdated = true;
        }
        if (request.locale() != null) {
            String userLocale = user.getLocale();
            if(userLocale == null || !userLocale.equals(request.locale())) {
                user.setLocale(request.locale());
                isUpdated = true;
            }
        }
        return isUpdated;
    }

    @NotNull
    public void storeLocale(@NotNull User user, @NotNull String locale) {
        user.setLocale(locale);
        userRepository.save(user);
    }

    @NotNull
    public String getLocale(@NotNull User user) {
        final String localeCode = user.getLocale();
        if (localeCode == null) {
            final String defaultLocale = "en";
            user.setLocale(defaultLocale);
            userRepository.save(user);
            return defaultLocale;
        }
        return localeCode;
    }
}
