package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.user.UserByDisplayNameRequest;
import com.otp.whiteboard.dto.user.UserUpdateRequest;
import com.otp.whiteboard.dto.user.UserDto;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.security.CustomUserDetails;
import com.otp.whiteboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.otp.whiteboard.api.Endpoint.USER_INTERNAL_API;

@RestController
@Tag(name = "User Management", description = "APIs for managing user profiles and information")
@RequestMapping(USER_INTERNAL_API)
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = """
                    This endpoint retrieves a list of all users in the system.
                    It returns a JSON array containing user details such as ID, display name, email, and profile picture URL.
                    """
    )
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<UserDto>> getAllUsers() {
        final List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Get user profile by display name",
            description = """
                    This endpoint retrieves a user's profile information based on their display name.
                    It accepts a JSON payload containing the display name and returns the corresponding user's details.
                    If no user is found with the provided display name, an appropriate error message is returned.
                    """
    )
    @PostMapping("/profile")
    @ResponseBody
    public ResponseEntity<UserDto> getUserProfileByDisplayName(
            @RequestBody
            @NotNull(message = "request must not be null")
            @Valid final UserByDisplayNameRequest request) {
        final UserDto userDto = userService.getUserProfileByDisplayName(request);
        return ResponseEntity.ok(userDto);
    }

    @Operation(
            summary = "Update user profile",
            description = """
                    This endpoint allows an authenticated user to update their profile information.
                    It accepts a JSON payload containing the fields to be updated, such as display name and profile picture URL.
                    Upon successful update, it returns the updated user details.
                    If the user is not authenticated, a 401 Unauthorized response is returned.
                    """
    )
    @PutMapping()
    @ResponseBody
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody @NotNull(message = "request must not be null") @Valid final UserUpdateRequest request,
            @AuthenticationPrincipal @Valid final CustomUserDetails currentUserDetails
    ) {
        final User currentUser = currentUserDetails != null ? currentUserDetails.user() : null;
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        final UserDto updatedUserProfile = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.ok(updatedUserProfile);
    }
}
