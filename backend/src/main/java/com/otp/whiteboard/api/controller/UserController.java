package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.response.UserDto;

import com.otp.whiteboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }

        @PostMapping("/profile")
        public ResponseEntity<UserDto> getUserProfileByDisplayName(@Valid @RequestBody String displayName) {
            UserDto userDto = userService.getUserProfilePicture(displayName);
            return ResponseEntity.ok(userDto);
        }

        @GetMapping("/all")
        public ResponseEntity<List<UserDto>> getAllUsers() {
            List<UserDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }

}
