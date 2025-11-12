package com.otp.whiteboard.service;

import com.otp.whiteboard.dto.auth.RegisterRequest;
import com.otp.whiteboard.dto.user.UserByDisplayNameRequest;
import com.otp.whiteboard.dto.user.UserUpdateRequest;
import com.otp.whiteboard.dto.user.UserDto;
import com.otp.whiteboard.enums.Status;
import com.otp.whiteboard.model.User;
import com.otp.whiteboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    private UserService userService;

    private final static Long ID = 2L;

    private final static String EMAIL = "test@email.com";
    private final static String EXIT_EMAIL = "exit@email.com";
    private final static String PHOTO_URL = "http://photo.url/image.jpg";
    private final static String DISPLAY_NAME = "Test User";
    private final static String UNEXIT_DISPLAY_NAME = "Not Found User";

    private final static String PASSWORD = "Test123!";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void init(){
        setupMockData();
        setupMocks();
        setupTestTarget();
    }

    void setupMockData(){
        testUser = new User();
        testUser.setId(ID);
        testUser.setEmail(EMAIL);
        testUser.setDisplayName(DISPLAY_NAME);
        testUser.setPassword(PASSWORD);
        testUser.setStatus(Status.ACTIVE);
    }

    void setupMocks(){
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(java.util.Optional.empty());
        when(userRepository.findByEmail(EXIT_EMAIL))
                .thenReturn(java.util.Optional.of(new User()));

        when(userRepository.findById(ID))
                .thenReturn(java.util.Optional.of(testUser));
        when(userRepository.findUserByDisplayName(DISPLAY_NAME))
                .thenReturn(java.util.Optional.of(testUser));
        when(userRepository.findAll()).thenReturn(java.util.List.of(testUser));
        when(userRepository.findByEmail(EXIT_EMAIL))
                .thenReturn(java.util.Optional.of(new User(2L, EXIT_EMAIL,DISPLAY_NAME,"encodedPassword",Status.ACTIVE)));

    }
    void setupTestTarget(){
        userService = new UserService(userRepository, passwordEncoder);
    }

    @DisplayName("Test user creation with valid data")
    @Test
    void testCreateUserWithValidData(){
        //given
        final RegisterRequest request = new RegisterRequest(EMAIL, PASSWORD, PHOTO_URL,DISPLAY_NAME,"vi" );
        //when
        final UserDto result = new UserDto( userService.createUser(request));
        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(DISPLAY_NAME, result.displayName());
        assertEquals(Status.ACTIVE, result.status());
        verify(passwordEncoder).encode(PASSWORD);
    }

    @DisplayName("As a user, I want to register with an existing email so that I receive an error message")
    @Test
    void testCreateUserWithExistingEmail(){
        //given
        final RegisterRequest request = new RegisterRequest(EXIT_EMAIL, PASSWORD, PHOTO_URL, DISPLAY_NAME,"vi" );
        final User existingUser = new User();
        existingUser.setEmail(EXIT_EMAIL);
        when(userRepository.findByEmail(EXIT_EMAIL))
                .thenReturn(java.util.Optional.of(existingUser));
        //When & Then
        try{
            userService.createUser(request);
        }catch (IllegalArgumentException e){
            assertEquals("User already exists with email: " + request.email(),e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As a user, I want to update my profile information so that my account details are current")
    @Test
    void testUpdateProfileWithFoundUser(){
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        //given
        final User updatedUser = testUser;
        final UserUpdateRequest request = new UserUpdateRequest("newemail@gmail.com", Status.ACTIVE, "http://photo.com", "testUser", "en");
        //when
        userService.updateUser(updatedUser.getId(), request);
        //then
        verify(userRepository, times(1)).save(userCaptor.capture());
        final User result = userCaptor.getValue();
        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(request.email(), result.getEmail());
    }

    @DisplayName("As a user, I want to update my profile information with a non-existing user ID so that I receive an error message")
    @Test
    void testUpdateProfileWithNotFoundUser() {
        //given
        final Long notFoundId = 3L;
        final UserUpdateRequest request = new UserUpdateRequest(EMAIL, null, null, null, null);
        //When & Then
        try {
            userService.updateUser(notFoundId, request);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As a user, I want to search for a user profile by display name so that I can view their details")
    @Test
    void getUserProfileByDisplayName() {
        //given
        final UserByDisplayNameRequest request = new UserByDisplayNameRequest(DISPLAY_NAME);
        //when
        final UserDto result = userService.getUserProfileByDisplayName(request);
        //then
        assertNotNull(result);
        assertEquals(request.displayName(), result.displayName());
    }
    @DisplayName("As a user, I want to search for a user profile by a non-existing display name so that I receive an error message")
    @Test
    void getUserProfileByNotExistingDisplayName() {
        //given
        final UserByDisplayNameRequest request = new UserByDisplayNameRequest(UNEXIT_DISPLAY_NAME);
        //When & Then
        try {
            userService.getUserProfileByDisplayName(request);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with display name: " + UNEXIT_DISPLAY_NAME, e.getMessage());
            return;
        }
        fail("Should have failed but didn't");
    }

    @DisplayName("As a user, I want to retrieve all users so that I can see a list of all registered users")
    @Test
    void getAllUsers() {
        final List<UserDto> result = userService.getAllUsers();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).id());
    }

    @DisplayName("As a user, I want to search for a user profile by display name so that I can view their details")
    @Test
    void getUserByEmail(){
        //when
        final UserDto result = new UserDto(userService.getUserByEmail(EXIT_EMAIL));
        //then
        assertNotNull(result);
        assertEquals(EXIT_EMAIL, result.email());
    }

}