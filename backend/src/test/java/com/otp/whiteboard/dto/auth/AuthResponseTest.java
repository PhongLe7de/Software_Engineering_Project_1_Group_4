package com.otp.whiteboard.dto.auth;

import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.model.User;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthResponseTest {

    private static final String VALID_EMAIL = "optValid@email.com";
    private static final String VALID_PASSWORD = "ValidPassword123!";
    private static final String TOKEN = "sampleToken";

    private User testUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init(){
        setupMockData();
    }

    void setupMockData(){
        String encodedPassword = passwordEncoder.encode(VALID_PASSWORD);
        testUser = mockUser(encodedPassword);
    }


    @Test
    @DisplayName("As user, I want to have access to my user information in the AuthResponse DTO")
    void testUserWithAuthResponse() {
        final UserDto userDto = new UserDto(testUser);
        final AuthResponse authResponse = new AuthResponse(userDto, TOKEN);
        assertEquals(userDto, authResponse.user());

    }

    @DisplayName("As user, I want to have access to my token in the AuthResponse DTO")
    @Test
    void testTokenWithAuthResponse() {
        final UserDto userDto = new UserDto(testUser);
        final AuthResponse authResponse = new AuthResponse(userDto, TOKEN);
        assertEquals(TOKEN, authResponse.token());
    }

    private static User mockUser(String password) {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setPassword(password);
        return user;
    }
}