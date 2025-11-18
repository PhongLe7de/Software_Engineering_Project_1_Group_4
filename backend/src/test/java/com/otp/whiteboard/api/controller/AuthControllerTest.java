package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.auth.AuthResponse;
import com.otp.whiteboard.dto.auth.LoginRequest;
import com.otp.whiteboard.dto.auth.RegisterRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.JwtUtil;
import com.otp.whiteboard.service.AuthService;
import com.otp.whiteboard.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {
    private AuthController authController;

    private static final String EMAIL = "user@test.com";
    private static final String PASSWORD = "password123";
    private static final String TOKEN = "token";
    private static final String PHOTO_URL = "http://photo.url";
    private static final String DISPLAY_NAME = "Test User";
    private static final String LOCALE = "en-US";

    private User testUser;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void init(){
        setupMockData();
        setupMocks();
        setupTestTarget();
    }

    void setupMockData(){
        testUser = mockUser();
    }

    void setupMocks(){
        when(userService.createUser(any())).thenReturn(testUser);
        when(jwtUtil.generateToken(any())).thenReturn(TOKEN);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when( userService.getUserByEmail(any())).thenReturn(testUser);

        when(authService.login(any())).thenReturn(new UserDto(testUser));

    }

    void setupTestTarget(){
        authController = new AuthController(userService, authenticationManager, jwtUtil);
    }

    @DisplayName("As a user, I want to register so that I can access the application")
    @Test
    void createUserReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest(
                DISPLAY_NAME,
                EMAIL,
                PHOTO_URL,
                PASSWORD,
                LOCALE
        );

        AuthResponse response = authController.createUser(request).getBody();

        assertNotNull(response);
        assertEquals(TOKEN, response.token());
    }

    @DisplayName("As a user, I want to log in so that I can access my account")
    @Test
    void loginReturnsAuthResponse() {
        LoginRequest request = new LoginRequest(
                EMAIL,
                PASSWORD,
                LOCALE
        );

        AuthResponse response = authController.login(request).getBody();

        assertNotNull(response);
        assertEquals(TOKEN, response.token());
    }

    private static User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setDisplayName(DISPLAY_NAME);
        user.setPhotoUrl(PHOTO_URL);
        user.setLocale(LOCALE);
        return user;
    }

}