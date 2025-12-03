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

    public Stroke() {
        // Default constructor for JPA
    }

    // Getters and Setters
    public Long getId() {return id;}

    public void setId(final Long id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(final Board board) {
        this.board = board;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public Long getThickness() {
        return thickness;
    }

    public void setThickness(final Long thickness) {
        this.thickness = thickness;
    }

    public DrawEventType getType() {
        return type;
    }

    public void setType(final DrawEventType type) {
        this.type = type;
    }

    public DrawingTool getTool() {
        return tool;
    }

    public void setTool(final DrawingTool tool) {
        this.tool = tool;
    }

    public Double getXCord() {
        return xCord;
    }

    public void setXCord(final Double xCord) {
        this.xCord = xCord;
    }

    public Double getYCord() {
        return yCord;
    }

    public void setYCord(final Double yCord) {
        this.yCord = yCord;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime now) {
        this.createdAt = now;
    }

}
