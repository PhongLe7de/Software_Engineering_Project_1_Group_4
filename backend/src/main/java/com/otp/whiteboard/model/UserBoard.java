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

    public UserBoard(User user, Board board) {
        this.user = user;
        this.board = board;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if( o == null || getClass() != o.getClass()) return false;
        UserBoard userBoard = (UserBoard) o;
        return Objects.equals(id, userBoard.id) && Objects.equals(user, userBoard.user) && Objects.equals(board, userBoard.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, board);
    }

}
