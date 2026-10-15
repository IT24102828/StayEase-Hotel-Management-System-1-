package com.stayease.model;

import java.math.BigDecimal;

/**
 * Concrete User subclass representing a Premium VIP Hotel Guest.
 * Discount rate = 10% (0.10).
 * Can manage rooms = false.
 *
 * Demonstrates Inheritance and Polymorphism.
 */
public class PremiumGuest extends User {

    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    public PremiumGuest() {
        super();
        setRole("PREMIUM");
    }

    public PremiumGuest(int id, String fullName, String email, String phone, String passwordHash) {
        super(id, fullName, email, phone, passwordHash, "PREMIUM");
    }

    @Override
    public BigDecimal getDiscountRate() {
        return DISCOUNT_RATE;
    }

    @Override
    public boolean canManageRooms() {
        return false;
    }
}
