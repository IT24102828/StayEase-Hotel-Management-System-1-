package com.stayease.model;

import java.math.BigDecimal;

/**
 * Concrete User subclass representing an Administrator.
 * Discount rate = 0%.
 * Can manage rooms = true.
 *
 * Demonstrates Inheritance and Polymorphism.
 */
public class AdminUser extends User {

    public AdminUser() {
        super();
        setRole("ADMIN");
    }

    public AdminUser(int id, String fullName, String email, String phone, String passwordHash) {
        super(id, fullName, email, phone, passwordHash, "ADMIN");
    }

    @Override
    public BigDecimal getDiscountRate() {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean canManageRooms() {
        return true;
    }
}
