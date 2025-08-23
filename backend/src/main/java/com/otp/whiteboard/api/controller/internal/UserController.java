package com.otp.whiteboard.api.controller.internal;
import com.otp.whiteboard.dto.request.UserRegisterRequest;
import com.otp.whiteboard.dto.request.response.UserDto;
import com.otp.whiteboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/api/internal/users")
@Validated
//@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "This endpoint registers a new user with the provided email and password."
    )
    @PostMapping
    @ResponseBody
    public UserDto registerUser(@RequestBody  @NotNull  @Valid UserRegisterRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Email and password must not be null");
        }
        return new UserDto(userService.registerUser(request.getEmail(), request.getPassword()));
    }

}
