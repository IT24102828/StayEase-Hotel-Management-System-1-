package com.stayease.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Room subclass representing a Single Room.
 * Nightly rate = base price (1.0x).
 * Sleeps: 1 guest.
 *
 * Demonstrates Inheritance and Polymorphic method implementations.
 */
public class SingleRoom extends Room {

    public SingleRoom() {
        super();
        setType("Single");
    }

    public SingleRoom(int id, String roomNumber, BigDecimal basePrice, String status, String description) {
        super(id, roomNumber, "Single", basePrice, status, description);
    }

    @Override
    public BigDecimal getNightlyRate() {
        if (getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return getBasePrice().setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getCapacity() {
        return 1;
    }

    @Override
    public String getFeatures() {
        return "1 Single Bed, Work Desk, En-suite Bathroom, High-speed Wi-Fi";
    }
}
