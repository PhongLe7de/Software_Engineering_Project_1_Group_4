package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.user.UserByDisplayNameRequest;
import com.otp.whiteboard.dto.user.UserUpdateRequest;
import com.otp.whiteboard.dto.user.UserDto;

import com.otp.whiteboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.otp.whiteboard.api.Endpoint.USER_INTERNAL_API;

@RestController
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
        List<UserDto> users = userService.getAllUsers();
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
        UserDto userDto = userService.getUserProfileByDisplayName(request);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Endpoint to update user details.
     *
     * @param id      The ID of the user to update.
     * @param request The user update request containing new details.
     */
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<UserDto> updateUser(
            @PathVariable
            @NotNull(message = "Id must not be null")
            @Positive(message = "Id must be positive") final Long id,
            @RequestBody @NotNull(message = "request must not be null") @Valid final UserUpdateRequest request) {
        UserDto updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
}
