package com.stayease.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Domain model representing a hotel room reservation.
 * Encapsulates booking details, date ranges, pricing calculation, and status.
 *
 * Owned by Member 2 (Epic 2: Guest and Reservation Management).
 */
public class Reservation {

    private int id;
    private int userId;
    private int roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private BigDecimal totalPrice;
    private String status; // CONFIRMED, CANCELLED

    // Associated presentation attributes (joined from Users and Rooms)
    private String guestName;
    private String guestEmail;
    private String roomNumber;
    private String roomType;
    private BigDecimal nightlyRate;

    public Reservation() {
        this.status = "CONFIRMED";
    }

    public Reservation(int id, int userId, int roomId, LocalDate checkIn, LocalDate checkOut,
                       int guests, BigDecimal totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.status = status != null ? status : "CONFIRMED";
    }

    // --- Business Methods ---

    /**
     * Calculates the number of nights between check-in and check-out.
     * Check-out day is exclusive.
     *
     * @return Number of nights stay
     */
    public long getNumberOfNights() {
        if (checkIn == null || checkOut == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Calculates total reservation price using room nightly rate, number of nights,
     * and polymorphic user discount rate:
     * total = nightlyRate * nights * (1 - membershipDiscount)
     *
     * @param room The booked Room instance
     * @param user The booking User instance
     * @param checkIn Reservation start date
     * @param checkOut Reservation departure date
     * @return Total price rounded to two decimal places
     */
    public static BigDecimal calculateTotalPrice(Room room, User user, LocalDate checkIn, LocalDate checkOut) {
        if (room == null || user == null || checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Room, user, and dates are required for price calculation");
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be strictly after check-in date");
        }

        BigDecimal nightlyRate = room.getNightlyRate();
        BigDecimal grossTotal = nightlyRate.multiply(BigDecimal.valueOf(nights));
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(user.getDiscountRate());
        return grossTotal.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(this.status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(this.status);
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        if (guests < 1 || guests > 10) {
            throw new IllegalArgumentException("Guest count must be between 1 and 10");
        }
        this.guests = guests;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.toUpperCase() : "CONFIRMED";
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(BigDecimal nightlyRate) {
        this.nightlyRate = nightlyRate != null ? nightlyRate.setScale(2, RoundingMode.HALF_UP) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", guests=" + guests +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
