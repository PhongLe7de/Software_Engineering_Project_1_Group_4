package com.otp.whiteboard.api.controller.internal;

import com.otp.whiteboard.dto.response.UserDto;
import com.otp.whiteboard.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.otp.whiteboard.api.Endpoint.USER_INTERNAL_API;

@RestController
@RequestMapping(USER_INTERNAL_API)
@Validated
@Tag(name = "User Management", description = "Protected user management operations - requires JWT authentication")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Get the profile information of the currently authenticated user"
    )
    @ResponseBody
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return new UserDto(userDetails.user());
    }

}
