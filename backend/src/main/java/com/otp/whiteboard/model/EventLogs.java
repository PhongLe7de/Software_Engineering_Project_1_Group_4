package com.otp.whiteboard.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "event_logs")
public class EventLogs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "payload", columnDefinition = "TEXT" )
    private String payload;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public EventLogs() {
    }

    public EventLogs(Board board, User user, String type, String payload, LocalDateTime createdAt) {
        this.board = board;
        this.user = user;
        this.type = type;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventLogs event = (EventLogs) o;
        return Objects.equals(id, event.id) && Objects.equals(board, event.board) && Objects.equals(user, event.user) && Objects.equals(type, event.type) && Objects.equals(payload, event.payload) && Objects.equals(createdAt, event.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, user, type, payload, createdAt);
    }

}
