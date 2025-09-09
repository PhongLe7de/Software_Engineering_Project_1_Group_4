package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.auth.LoginRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {
    private AuthService authService;

    private static final String VALID_EMAIL = "optValid@email.com";
    private static final String INVALID_EMAIL = "invalid@email.com";

    private static final String VALID_PASSWORD = "ValidPassword123!";
    private static final String INVALID_PASSWORD = "InvalidPassword123!";

    private User testUser;
    private String encodedPassword;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void init(){
        setupMockData();
        setupMocks();
        setupTestTarget();
    }

    void setupMockData(){
        encodedPassword = passwordEncoder.encode(VALID_PASSWORD);
        testUser = mockUser(encodedPassword);
    }

    void setupMocks(){
        when(userRepository.findByEmail(VALID_EMAIL))
                .thenReturn(java.util.Optional.of(testUser));
        when(userRepository.findByEmail(INVALID_EMAIL))
                .thenReturn(java.util.Optional.empty());
        when(passwordEncoder.matches(VALID_PASSWORD, encodedPassword)).thenReturn(true);
        when(passwordEncoder.matches(INVALID_PASSWORD, encodedPassword)).thenReturn(false);

    }

    void setupTestTarget(){
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @DisplayName("As a user, I want to log in with valid credentials so that I can access my account")
    @Test
    void testLoginWithValidCredentials() {
        //given
        final LoginRequest request = new LoginRequest(VALID_EMAIL, VALID_PASSWORD);
        //when
        UserDto userDto = authService.login(request);
        //then
        assertNotNull(userDto);
        assertEquals(VALID_EMAIL, userDto.email());
        verify(userRepository).findByEmail(VALID_EMAIL);
        verify(passwordEncoder).matches(VALID_PASSWORD,encodedPassword);
    }

    @DisplayName("As a user, I want to log in with invalid user so that I receive an error message")
    @Test
    void testLoginWithInvalidUser() {
        //given
        final LoginRequest request = new LoginRequest(INVALID_EMAIL, VALID_PASSWORD);

        //when
        try {
            authService.login(request);
        } catch (IllegalArgumentException e) {
            //then
            assertEquals("Invalid email or password", e.getMessage());
            verify(userRepository).findByEmail(INVALID_EMAIL);
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As a user, I want to log in with invalid password so that I receive an error message")
    @Test
    void testLoginWithInvalidPassword() {
        //given
        final LoginRequest request = new LoginRequest(VALID_EMAIL, INVALID_PASSWORD);
        //when
        try {
            authService.login(request);
        } catch (IllegalArgumentException e) {
            //then
            assertEquals("Invalid email or password", e.getMessage());
            verify(userRepository).findByEmail(VALID_EMAIL);
            verify(passwordEncoder).matches(INVALID_PASSWORD,encodedPassword);
            return;
        }
        fail("Should have failed but didn't");

    }

    private static User mockUser(String password) {
        User user = new User();
        user.setEmail(AuthServiceTest.VALID_EMAIL);
        user.setPassword(password);
        return user;
    }
}