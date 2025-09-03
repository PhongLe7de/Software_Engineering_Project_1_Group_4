package com.otp.whiteboard.dto.response;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.enums.Status;

import java.time.LocalDateTime;

public class RegisterDto {
    private UserInfo user;

    public RegisterDto(User user) {
        this.user = new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPhotoUrl(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public record UserInfo(
            Long id,
            String email,
            String displayName,
            String photoUrl,
            Status status,
            LocalDateTime createdAt
    ) {}
}