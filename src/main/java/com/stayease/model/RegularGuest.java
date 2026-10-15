package com.stayease.model;

import java.math.BigDecimal;

/**
 * Concrete User subclass representing a Regular Hotel Guest.
 * Discount rate = 0%.
 * Can manage rooms = false.
 *
 * Demonstrates Inheritance and Polymorphism.
 */
public class RegularGuest extends User {

    public RegularGuest() {
        super();
        setRole("REGULAR");
    }

    public RegularGuest(int id, String fullName, String email, String phone, String passwordHash) {
        super(id, fullName, email, phone, passwordHash, "REGULAR");
    }

    @Override
    public BigDecimal getDiscountRate() {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean canManageRooms() {
        return false;
    }
}
