package com.otp.whiteboard.dto.auth;

import com.otp.whiteboard.dto.user.UserDto;

public record AuthResponse(
        UserDto user,
        String token
){
}
