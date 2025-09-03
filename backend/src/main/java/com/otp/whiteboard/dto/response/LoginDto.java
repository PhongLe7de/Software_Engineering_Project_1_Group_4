package com.otp.whiteboard.dto.response;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.enums.Status;

import java.time.LocalDateTime;

public class LoginDto {
    private String token;
    private String type = "Bearer";
    private UserInfo user;

    public LoginDto(String token, User user) {
        this.token = token;
        this.user = new UserInfo(user);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public static class UserInfo {
        private Long id;
        private String email;
        private String displayName;
        private String photoUrl;
        private Status status;
        private LocalDateTime createdAt;

        public UserInfo(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.displayName = user.getDisplayName();
            this.photoUrl = user.getPhotoUrl();
            this.status = user.getStatus();
            this.createdAt = user.getCreatedAt();
        }

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}