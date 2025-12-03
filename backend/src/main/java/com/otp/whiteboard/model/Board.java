package com.otp.whiteboard.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "owner_id")
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

    @Column(name = "custom_message", length = 500)
    private String customMessage;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserBoard> users = new HashSet<>();

    public Board() {
    }

    public Board(final String name, final Long ownerId, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.name = name;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Board(final String name, final Long ownerId,final  String customMessage, final LocalDateTime createdAt,final  LocalDateTime updatedAt) {
        this.name = name;
        this.ownerId = ownerId;
        this.customMessage = customMessage;
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

    public void setName(final String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(final Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(final String customMessage) {
        this.customMessage = customMessage;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
    public void setUserIds(final List<Long> userIds) {
        this.userIds = userIds;
    }
    public Set<UserBoard> getUsers() {
        return users;
    }
    public void setUsers(final Set<UserBoard> users) {
        this.users = users;
    }

    public Integer getNumberOfStrokes() {
        return numberOfStrokes;
    }
    public void setNumberOfStrokes(final Integer numberOfStrokes) {
        this.numberOfStrokes = numberOfStrokes;
    }

    @PrePersist
    protected void onCreate() {
        final LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addUser(final User user) {
        if(!userIds.contains(user.getId())) {
            userIds.add(user.getId());
            final UserBoard ub = new UserBoard(user, this);
            users.add(ub);
        }
    }

    public void removeUser(final User user) {
        userIds.remove(user.getId());
        users.removeIf(ub -> ub.getUser().equals(user));
    }

    public void incrementStrokes() { numberOfStrokes++; }
}

