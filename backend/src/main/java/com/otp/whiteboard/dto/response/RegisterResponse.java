package com.otp.whiteboard.dto.response;

import com.otp.whiteboard.model.User;
import com.otp.whiteboard.enums.Status;

import java.time.LocalDateTime;

public class RegisterResponse {
    private String message;
    private UserInfo user;

    public RegisterResponse(String message, User user) {
        this.message = message;
        this.user = new UserInfo(user);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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