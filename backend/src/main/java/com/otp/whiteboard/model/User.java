package com.otp.whiteboard.model;

import com.otp.whiteboard.common.annotation.LibraryUseConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user",
        indexes = {
                @Index(name = "idx_user_status", columnList = "status"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_email", columnNames = "email"),

        })
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "username", unique = true, length = 50)
        private String username;

        @Column(name = "email", nullable = false, unique = true, length = 100)
        private String email;

        @Column(name = "password", nullable = false, length = 255)
        private String password;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @LibraryUseConstructor
        public User() {
        }

        public User(String email, String password) {
                this.email = email;
                this.password = password;
        }

        //Getters and Setters
        public Long getId() {
                return id;
        }
        public void setId(Long id) {
                this.id = id;
        }
        public String getUsername() {
                return username;
        }
        public void setUsername(String username) {
                this.username = username;
        }
        public String getEmail() {
                return email;
        }
        public void setEmail(String email) {
                this.email = email;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                User user = (User) o;
                return Objects.equals(id, user.id) &&  Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(createdAt, user.createdAt);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, email, password, createdAt);
        }
}