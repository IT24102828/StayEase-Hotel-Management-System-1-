package com.stayease.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Room subclass representing a Suite Room.
 * Nightly rate = base price * 1.25 + $20.00 service charge.
 * Sleeps: 4 guests.
 *
 * Demonstrates Inheritance and Polymorphic method implementations.
 */
public class SuiteRoom extends Room {

    private static final BigDecimal MULTIPLIER = new BigDecimal("1.25");
    private static final BigDecimal SERVICE_SURCHARGE = new BigDecimal("20.00");

    public SuiteRoom() {
        super();
        setType("Suite");
    }

    public SuiteRoom(int id, String roomNumber, BigDecimal basePrice, String status, String description) {
        super(id, roomNumber, "Suite", basePrice, status, description);
    }

    @Override
    public BigDecimal getNightlyRate() {
        if (getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        return getBasePrice().multiply(MULTIPLIER)
                .add(SERVICE_SURCHARGE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int getCapacity() {
        return 4;
    }

    @Override
    public String getFeatures() {
        return "Separate Living Area, King Bed + Sofa Bed, Balcony, Minibar, Luxury Bath";
    }
}
