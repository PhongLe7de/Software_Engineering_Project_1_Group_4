package com.otp.whiteboard.api.controller;

import com.otp.whiteboard.dto.response.UserDto;

import com.otp.whiteboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
