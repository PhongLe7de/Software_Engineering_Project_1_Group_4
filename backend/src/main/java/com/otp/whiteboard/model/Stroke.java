package com.otp.whiteboard.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "strokes")
public class Stroke implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "color")
    private String color;

    @Column(name = "thickness")
    private Long thickness;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "tool")
    private String tool;

    @Column(name = "x_cord", nullable = false)
    private Float x_cord;

    @Column(name = "y_cord", nullable = false)
    private Float y_cord;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Stroke() {

    }

    public Stroke(Board board, User user, String color, Long thickness, String type, String tool, Float x_cord, Float y_cord, LocalDateTime createdAt) {
        this.board = board;
        this.user = user;
        this.color = color;
        this.thickness = thickness;
        this.type = type;
        this.tool = tool;
        this.x_cord = x_cord;
        this.y_cord = y_cord;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getThickness() {
        return thickness;
    }

    public void setThickness(Long thickness) {
        this.thickness = thickness;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public Float getX_cord() {
        return x_cord;
    }

    public void setX_cord(Float x_cord) {
        this.x_cord = x_cord;
    }

    public Float getY_cord() {
        return y_cord;
    }

    public void setY_cord(Float y_cord) {
        this.y_cord = y_cord;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stroke stroke = (Stroke) o;
        return Objects.equals(id, stroke.id) && Objects.equals(board, stroke.board) && Objects.equals(user, stroke.user) && Objects.equals(color, stroke.color) && Objects.equals(thickness, stroke.thickness) && Objects.equals(type, stroke.type) && Objects.equals(tool, stroke.tool) && Objects.equals(x_cord, stroke.x_cord) && Objects.equals(y_cord, stroke.y_cord) && Objects.equals(createdAt, stroke.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, user, color, thickness, type, tool, x_cord, y_cord, createdAt);
    }
}
