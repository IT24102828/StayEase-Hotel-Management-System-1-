package com.stayease.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Room subclass representing a Double Room.
 * Nightly rate = base price * 1.10 (+10%).
 * Sleeps: 2 guests.
 *
 * Demonstrates Inheritance and Polymorphic method implementations.
 */
public class DoubleRoom extends Room {

    private static final BigDecimal MULTIPLIER = new BigDecimal("1.10");

    public DoubleRoom() {
        super();
        setType("Double");
    }

    public DoubleRoom(int id, String roomNumber, BigDecimal basePrice, String status, String description) {
        super(id, roomNumber, "Double", basePrice, status, description);
    }

    @Override
    public BigDecimal getNightlyRate() {
        if (getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return getBasePrice().multiply(MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getCapacity() {
        return 2;
    }

    @Override
    public String getFeatures() {
        return "1 Queen Bed, Sitting Area, City View, Smart TV, Premium Amenities";
    }
}
