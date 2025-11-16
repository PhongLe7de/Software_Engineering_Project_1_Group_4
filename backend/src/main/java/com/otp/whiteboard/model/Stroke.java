package com.otp.whiteboard.model;

import com.otp.whiteboard.enums.DrawEventType;
import com.otp.whiteboard.enums.DrawingTool;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    private DrawEventType type;

    @Column(name = "tool")
    private DrawingTool tool;

    @Column(name = "x_cord", nullable = false)
    private Double xCord;

    @Column(name = "y_cord", nullable = false)
    private Double yCord;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Stroke() {}

    public Stroke(@NotNull @Valid final Board board,
                  @NotNull @Valid final User user,
                  @NotBlank final String color,
                  @NotNull final Long thickness,
                  @NotNull @Valid final DrawEventType type,
                  @NotNull @Valid final DrawingTool tool,
                  @NotNull final Double xCord,
                  @NotNull final Double yCord,
                  @NotNull @Valid final LocalDateTime createdAt) {
        this.board = board;
        this.user = user;
        this.color = color;
        this.thickness = thickness;
        this.type = type;
        this.tool = tool;
        this.xCord = xCord;
        this.yCord = yCord;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {return id;}

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

    public DrawEventType getType() {
        return type;
    }

    public void setType(DrawEventType type) {
        this.type = type;
    }

    public DrawingTool getTool() {
        return tool;
    }

    public void setTool(DrawingTool tool) {
        this.tool = tool;
    }

    public Double getXCord() {
        return xCord;
    }

    public void setXCord(Double xCord) {
        this.xCord = xCord;
    }

    public Double getYCord() {
        return yCord;
    }

    public void setYCord(Double yCord) {
        this.yCord = yCord;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime now) {
        this.createdAt = now;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stroke stroke = (Stroke) o;
        return Objects.equals(id, stroke.id) && Objects.equals(board, stroke.board) && Objects.equals(user, stroke.user) && Objects.equals(color, stroke.color) && Objects.equals(thickness, stroke.thickness) && Objects.equals(type, stroke.type) && Objects.equals(tool, stroke.tool) && Objects.equals(xCord, stroke.xCord) && Objects.equals(yCord, stroke.yCord) && Objects.equals(createdAt, stroke.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, user, color, thickness, type, tool, xCord, yCord, createdAt);
    }

}
