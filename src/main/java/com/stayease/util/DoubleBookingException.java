package com.stayease.util;

/**
 * Exception thrown when a reservation attempt fails due to concurrent double-booking.
 */
public class DoubleBookingException extends Exception {

    public DoubleBookingException(String message) {
        super(message);
    }
}
