package com.otp.whiteboard.model;

import com.otp.whiteboard.enums.Role;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_boards")
public class UserBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Role role = Role.EDITOR;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id", nullable = false)
    private Board board;

    public UserBoard() {
    }

    public UserBoard(final User user,final  Board board) {
        this.user = user;
        this.board = board;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(final Board board) {
        this.board = board;
    }

    public void setRole(final Role role) {
        this.role = role;
    }
}
