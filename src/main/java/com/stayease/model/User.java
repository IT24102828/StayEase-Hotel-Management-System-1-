package com.stayease.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract domain model representing a system user.
 * Demonstrates:
 *  - Encapsulation (private fields, validation in setters)
 *  - Abstraction & Polymorphism (polymorphic discount & authorization methods)
 *  - Factory Method Pattern (User.create)
 *
 * Owned by Member 2 (Epic 2: Guest and Reservation Management).
 */
public abstract class User {

    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String role;

    protected User() {
    }

    protected User(int id, String fullName, String email, String phone, String passwordHash, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // --- Abstract Polymorphic Methods ---

    /**
     * Polymorphic discount rate applicable to room reservations.
     * Regular/Admin: 0%, Premium: 10%.
     *
     * @return Discount rate as BigDecimal (e.g. 0.00 or 0.10)
     */
    public abstract BigDecimal getDiscountRate();

    /**
     * Determines whether this user has administrative access to manage hotel rooms.
     *
     * @return true for Admin, false for guests
     */
    public abstract boolean canManageRooms();

    // --- Factory Method ---

    /**
     * Factory method creating the appropriate User subclass instance based on role string.
     *
     * @param role User role ("ADMIN", "REGULAR", "PREMIUM")
     * @return Specialized User instance
     */
    public static User create(String role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        return switch (role.trim().toUpperCase()) {
            case "ADMIN" -> new AdminUser();
            case "REGULAR" -> new RegularGuest();
            case "PREMIUM" -> new PremiumGuest();
            default -> throw new IllegalArgumentException("Unknown user role: " + role);
        };
    }

    /**
     * Factory method constructing and populating a specialized User subclass.
     */
    public static User create(int id, String fullName, String email, String phone, String passwordHash, String role) {
        User user = create(role);
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordHash);
        user.setRole(role != null ? role.toUpperCase() : "REGULAR");
        return user;
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName != null && fullName.length() > 100) {
            throw new IllegalArgumentException("Full name cannot exceed 100 characters");
        }
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.length() > 100) {
            throw new IllegalArgumentException("Email cannot exceed 100 characters");
        }
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null && phone.length() > 20) {
            throw new IllegalArgumentException("Phone cannot exceed 20 characters");
        }
        this.phone = phone != null ? phone.trim() : null;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.trim().toUpperCase() : null;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isPremium() {
        return "PREMIUM".equalsIgnoreCase(this.role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", discountRate=" + getDiscountRate() +
                '}';
    }
}
