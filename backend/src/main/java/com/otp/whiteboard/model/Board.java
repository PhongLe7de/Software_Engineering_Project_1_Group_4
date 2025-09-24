package com.otp.whiteboard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true, name = "owner_id")
    private Long ownerId;

    @ElementCollection
    @CollectionTable(name = "board_user_ids", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "user_id")
    private List<Long> userIds = new ArrayList<>();

    @Column(name = "amount_of_users")
    private Integer amountOfUsers = 0;

    @Column(name = "number_of_strokes", columnDefinition = "INT DEFAULT 0")
    private Integer numberOfStrokes = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserBoard> users = new HashSet<>();

    public Board() {
    }

    public Board(String name, Long ownerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
    public Set<UserBoard> getUsers() {
        return users;
    }
    public void setUsers(Set<UserBoard> users) {
        this.users = users;
    }

    public Integer getNumberOfStrokes() {
        return numberOfStrokes;
    }
    public void setNumberOfStrokes(Integer numberOfStrokes) {
        this.numberOfStrokes = numberOfStrokes;
    }

    public Integer getAmountOfUsers() {
        return userIds.size();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addUser(User user) {
        if(!userIds.contains(user.getId())) {
            userIds.add(user.getId());
            UserBoard ub = new UserBoard(user, this);
            users.add(ub);
        }
    }

    public void removeUser(User user) {
        userIds.remove(user.getId());
        users.removeIf(ub -> ub.getUser().equals(user));
    }

    public void incrementStrokes() { numberOfStrokes++; }

    public void decrementStrokes() { if(numberOfStrokes > 0) numberOfStrokes--; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id) &&
                Objects.equals(name, board.name) &&
                Objects.equals(ownerId, board.ownerId) &&
                Objects.equals(createdAt, board.createdAt) &&
                Objects.equals(updatedAt, board.updatedAt) &&
                Objects.equals(userIds, board.userIds) &&
                Objects.equals(numberOfStrokes, board.numberOfStrokes) &&
                Objects.equals(amountOfUsers, board.amountOfUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ownerId, createdAt, updatedAt, userIds, amountOfUsers, numberOfStrokes);
    }
}

