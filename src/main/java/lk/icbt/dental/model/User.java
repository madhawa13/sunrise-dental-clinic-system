package lk.icbt.dental.model;

import java.time.LocalDateTime;

/**
 * Represents a system user such as
 * a receptionist or dentist.
 */
public class User {

    public static final String ROLE_RECEPTIONIST =
            "RECEPTIONIST";

    public static final String ROLE_DENTIST =
            "DENTIST";

    private Long userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String role;
    private String email;
    private String phone;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public User() {
        this.active = true;
    }

    /**
     * Constructor for creating a system user.
     */
    public User(
            String username,
            String passwordHash,
            String fullName,
            String role,
            String email,
            String phone) {

        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.active = true;
    }

    /**
     * Constructor for loading a user
     * from the database.
     */
    public User(
            Long userId,
            String username,
            String passwordHash,
            String fullName,
            String role,
            String email,
            String phone,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(
            String username) {

        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(
            String passwordHash) {

        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName) {

        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }

    /**
     * Returns true when this user is a dentist.
     */
    public boolean isDentist() {
        return ROLE_DENTIST.equals(role);
    }

    /**
     * Returns true when this user is a receptionist.
     */
    public boolean isReceptionist() {
        return ROLE_RECEPTIONIST.equals(role);
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                + ", username='" + username + '\''
                + ", fullName='" + fullName + '\''
                + ", role='" + role + '\''
                + ", active=" + active
                + '}';
    }
}