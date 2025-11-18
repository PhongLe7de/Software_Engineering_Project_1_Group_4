package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.user.UserByDisplayNameRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.dto.user.UserUpdateRequest;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.CustomUserDetails;
import com.otp.whiteboard.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {
    private static final Long USER_ID = 1L;
    private static final String DISPLAY_NAME = "Test User";
    private static final String EMAIL = "";
    private static final String PHOTO_URL = "http://photo.url";
    private static final String NEW_PHOTO_URL = "http://new.photo.url";
    private static final String NEW_DISPLAY_NAME = "Test User";
    private static final String PASSWORD = "hashed password";

    private UserController userController;
    private User testUser;
    private User updatedUser;

    @Mock
    private UserService userService;

    @BeforeEach
    void init() {
        setupMockData();
        setupMocks();
        setupTestTarget();
    }

    private void setupTestTarget() {
        userController = new UserController(userService);
    }

    private void setupMocks() {
        when(userService.getAllUsers()).thenReturn(List.of(new UserDto(testUser)));
        when(userService.getUserProfileByDisplayName(any(UserByDisplayNameRequest.class)))
                .thenReturn(new UserDto(testUser));
        when(userService.updateUser(any(), any(UserUpdateRequest.class)))
                .thenReturn(new UserDto(updatedUser));
    }

    private void setupMockData() {
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setDisplayName(DISPLAY_NAME);
        testUser.setEmail(EMAIL);
        testUser.setPhotoUrl(PHOTO_URL);
        testUser.setPassword(PASSWORD);

        updatedUser = new User();
        updatedUser.setId(USER_ID);
        updatedUser.setDisplayName(NEW_DISPLAY_NAME);
        updatedUser.setEmail(EMAIL);
        updatedUser.setPhotoUrl(NEW_PHOTO_URL);
        updatedUser.setPassword(PASSWORD);
    }

    @DisplayName("As a developer, I want to get all users so that I can display them in the user list")
    @Test
    void getAllUsers() {
        final ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(DISPLAY_NAME, response.getBody().get(0).displayName());
        assertEquals(EMAIL, response.getBody().get(0).email());
    }

    @DisplayName("As a user, I want to get a user profile by display name so that I can view their information")
    @Test
    void getUserProfileByDisplayName() {
        final UserByDisplayNameRequest request = new UserByDisplayNameRequest(DISPLAY_NAME);

        final ResponseEntity<UserDto> response = userController.getUserProfileByDisplayName(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(DISPLAY_NAME, response.getBody().displayName());
        assertEquals(EMAIL, response.getBody().email());
    }

    @Test
    void updateUserReturnErrorWhenNotAuthenticated() {
        final UserUpdateRequest updateRequest = new UserUpdateRequest(
                null,
                NEW_PHOTO_URL,
                NEW_DISPLAY_NAME,
                null,
                null
        );

        final ResponseEntity<UserDto> response = userController.updateUserProfile(updateRequest, null);

        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @DisplayName("As an authenticated user, I want to update my profile so that I can keep my information current")
    @Test
    void updateUserProfile() {
        final User currentUser = new User();
        currentUser.setId(USER_ID);
        final CustomUserDetails customUser = new CustomUserDetails(currentUser);

        final UserUpdateRequest updateRequest = new UserUpdateRequest(
                null,
                NEW_PHOTO_URL,
                NEW_DISPLAY_NAME,
                null, null
        );

        final ResponseEntity<UserDto> response = userController.updateUserProfile(updateRequest, customUser);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(NEW_DISPLAY_NAME, response.getBody().displayName());
        assertEquals(NEW_PHOTO_URL, response.getBody().photoUrl());
    }
}
