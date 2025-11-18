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

    private static final Long ID = 2L;

    private static final String EMAIL = "test@email.com";
    private static final String EXIST_EMAIL = "exist@email.com";
    private static final String PHOTO_URL = "http://photo.url/image.jpg";
    private static final String DISPLAY_NAME = "Test User";
    private static final String UNEXIST_DISPLAY_NAME = "Not Found User";

    private static final String TEST_USER_CREDENTIAL = "Test123!";

    private static final String FAIL_MESSAGE = "Should have failed but didn't";

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
        testUser.setPassword(TEST_USER_CREDENTIAL);
        testUser.setStatus(Status.ACTIVE);
    }

    void setupMocks(){
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });
        when(passwordEncoder.encode(TEST_USER_CREDENTIAL)).thenReturn("encodedPassword");
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(java.util.Optional.empty());
        when(userRepository.findByEmail(EXIST_EMAIL))
                .thenReturn(java.util.Optional.of(new User()));

        when(userRepository.findById(ID))
                .thenReturn(java.util.Optional.of(testUser));
        when(userRepository.findUserByDisplayName(DISPLAY_NAME))
                .thenReturn(java.util.Optional.of(testUser));
        when(userRepository.findAll()).thenReturn(java.util.List.of(testUser));
        when(userRepository.findByEmail(EXIST_EMAIL))
                .thenReturn(java.util.Optional.of(new User(2L, EXIST_EMAIL,DISPLAY_NAME,"encodedPassword",Status.ACTIVE)));

    }
    void setupTestTarget(){
        userService = new UserService(userRepository, passwordEncoder);
    }

    @DisplayName("As a user, I want to create a new user but saving fails so that an exception is rethrown")
    @Test
    void createUserThrowsExceptionRethrown() {
        // given
        final RegisterRequest request = new RegisterRequest(EMAIL, TEST_USER_CREDENTIAL, PHOTO_URL, DISPLAY_NAME, "en");
        //when
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("save failed"));

        // then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.createUser(request));

        assertEquals("save failed", ex.getMessage());
    }

    @DisplayName("createUser should set provided locale when not null")
    @Test
    void testCreateUserWithValidDataButMissingLocale() {
        RegisterRequest request = new RegisterRequest(
                EMAIL,
                TEST_USER_CREDENTIAL,
                PHOTO_URL,
                DISPLAY_NAME,
                null
        );
        //when & then
        final UserDto result = new UserDto( userService.createUser(request));

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(DISPLAY_NAME, result.displayName());
        assertEquals(Status.ACTIVE, result.status());
        verify(passwordEncoder).encode(TEST_USER_CREDENTIAL);
    }

    @DisplayName("Test user creation with valid data")
    @Test
    void testCreateUserWithValidData(){
        //given
        final RegisterRequest request = new RegisterRequest(EMAIL, TEST_USER_CREDENTIAL, PHOTO_URL,DISPLAY_NAME,"vi" );
        //when & then
        final UserDto result = new UserDto( userService.createUser(request));

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(DISPLAY_NAME, result.displayName());
        assertEquals(Status.ACTIVE, result.status());
        verify(passwordEncoder).encode(TEST_USER_CREDENTIAL);
    }

    @DisplayName("As a user, I want to register with an existing email so that I receive an error message")
    @Test
    void testCreateUserWithExistingEmail(){
        //given
        final RegisterRequest request = new RegisterRequest(EXIST_EMAIL, TEST_USER_CREDENTIAL, PHOTO_URL, DISPLAY_NAME,"vi" );
        final User existingUser = new User();
        existingUser.setEmail(EXIST_EMAIL);
        when(userRepository.findByEmail(EXIST_EMAIL))
                .thenReturn(java.util.Optional.of(existingUser));
        //When & Then
        try{
            userService.createUser(request);
        }catch (IllegalArgumentException e){
            assertEquals("User already exists with email: " + request.email(),e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("updateUser should catch and rethrow exceptions from repository.save")
    @Test
    void updateUserThrowsExceptionRethrown() {
        // given
        final UserUpdateRequest req = new UserUpdateRequest(Status.ACTIVE, null, null, null);
        // when
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("update failed"));
        // then
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> userService.updateUser(ID, req));

        assertEquals("update failed", e.getMessage());
    }

    @DisplayName("As a user, I want to update my profile information with no fields updated so that no changes are made")
    @Test
    void updateUserWithNoFieldsUpdated() {
        // given
        final UserUpdateRequest req = new UserUpdateRequest(null, null, null, null);

        // when & then
        final UserDto result = userService.updateUser(ID, req);

        assertNotNull(result);
        verify(userRepository, times(0)).save(any());
    }


    @DisplayName("As a user, I want to update my profile information so that my account details are current")
    @Test
    void testUpdateProfileWithFoundUser(){
        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        //given
        final User updatedUser = testUser;
        final UserUpdateRequest request = new UserUpdateRequest(Status.ACTIVE, "https://photo.com", "testUser", "en");
        //when
        userService.updateUser(updatedUser.getId(), request);
        //then
        verify(userRepository, times(1)).save(userCaptor.capture());
        final User result = userCaptor.getValue();
        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
    }

    @DisplayName("As a user, I want to update my profile information with a non-existing user ID so that I receive an error message")
    @Test
    void testUpdateProfileWithNotFoundUser() {
        //given
        final Long notFoundId = 3L;
        final UserUpdateRequest request = new UserUpdateRequest(null, null, null, null);
        //When & Then
        try {
            userService.updateUser(notFoundId, request);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with ID: " + notFoundId, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
    }

    @DisplayName("As a user, I want to update my profile information when my current locale is null so that my locale preference is set")
    @Test
    void testUpdateUserFieldsWhenUserLocaleIsNull() {
        //given
        final User user = new User();
        user.setLocale(null);
        final UserUpdateRequest request = new UserUpdateRequest(
                null, null, null, "fi"
        );
        //when & then
        final boolean updated = userService.updateUserFields(user, request);

        assertTrue(updated);
        assertEquals("fi", user.getLocale());
    }

    @DisplayName("As a user, I want to update my profile information when my current locale is different so that my locale preference is updated")
    @Test
    void testUpdateUserFieldsWithDifferentLocale(){
        //given
        final User user = new User();
        user.setLocale("en");

        final UserUpdateRequest req = new UserUpdateRequest(
                null, null, null, "fi"
        );
        //when & then
        boolean updated = userService.updateUserFields(user, req);

        assertTrue(updated);
        assertEquals("fi", user.getLocale());
    }
    @Test
    void testUpdateUserFieldsWithSameLocale() {
        //given
        final User user = new User();
        user.setLocale("en");
        final UserUpdateRequest request = new UserUpdateRequest(
                null, null, null, "en"
        );
        //when & then
        boolean updated = userService.updateUserFields(user, request);

        assertFalse(updated);
        assertEquals("en", user.getLocale());
    }


    @DisplayName("As a user, I want to search for a user profile by display name but repository throws an exception so that the exception is rethrown")
    @Test
    void getUserProfileByDisplayNameThrowException() {
        // given
        final UserByDisplayNameRequest req = new UserByDisplayNameRequest(DISPLAY_NAME);
        //when
        when(userRepository.findUserByDisplayName(DISPLAY_NAME))
                .thenThrow(new RuntimeException("db failure"));
        // then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserProfileByDisplayName(req));

        assertEquals("db failure", ex.getMessage());
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
        final UserByDisplayNameRequest request = new UserByDisplayNameRequest(UNEXIST_DISPLAY_NAME);
        //When & Then
        try {
            userService.getUserProfileByDisplayName(request);
        } catch (IllegalArgumentException e) {
            assertEquals("User not found with display name: " + UNEXIST_DISPLAY_NAME, e.getMessage());
            return;
        }
        fail(FAIL_MESSAGE);
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
        final UserDto result = new UserDto(userService.getUserByEmail(EXIST_EMAIL));
        //then
        assertNotNull(result);
        assertEquals(EXIST_EMAIL, result.email());
    }

    @DisplayName("As a user, I want to store locale preference so that the application can provide localized content")
    @Test
    void storeLocale(){
        //when
        userService.storeLocale(testUser, "en");
        //then
        verify(userRepository,times(1)).save(any(User.class));
    }

    @DisplayName("As a user, I want to rdetireve the default locale if no locale is set so that the application can provide content in the default language")
    @Test
    void getLocale(){
        //when
        final String locale = userService.getLocale(testUser);
        //then
        assertNotNull(locale);
        assertEquals("en", locale);
    }

    @DisplayName("As a user, I want to retrieve my set locale preference so that the application can provide content in my chosen language")
    @Test
    void getSetLocale() {
        //given
        testUser.setLocale("vi");
        //when
        final String locale = userService.getLocale(testUser);
        //then
        assertNotNull(locale);
        assertEquals("vi", locale);


    }
}