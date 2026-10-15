package com.stayease.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Abstract domain model representing a hotel room.
 * Demonstrates:
 *  - Encapsulation (private fields with controlled accessors and mutators)
 *  - Abstraction (abstract methods defining polymorphic behavior)
 *  - Factory Method Pattern (Room.create)
 *
 * Owned by Member 1 (Epic 1: Room Management).
 */
public abstract class Room {

    private int id;
    private String roomNumber;
    private String type;
    private BigDecimal basePrice;
    private String status;
    private String description;

    /**
     * Default constructor for framework use.
     */
    protected Room() {
    }

    /**
     * Fully parameterized constructor.
     */
    protected Room(int id, String roomNumber, String type, BigDecimal basePrice, String status, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.basePrice = basePrice;
        this.status = status;
        this.description = description;
    }

    // --- Abstract Polymorphic Methods ---

    /**
     * Calculates the actual nightly rate using room-specific pricing formula.
     * Implemented polymorphically by subclasses.
     *
     * @return Nightly rate rounded to two decimal places
     */
    public abstract BigDecimal getNightlyRate();

    /**
     * Maximum number of guests accommodated by this room type.
     *
     * @return Maximum guest capacity
     */
    public abstract int getCapacity();

    /**
     * Detailed amenities and features included with this room type.
     *
     * @return Descriptive feature string
     */
    public abstract String getFeatures();

    // --- Factory Method ---

    /**
     * Factory method creating the appropriate Room subclass instance based on room type.
     *
     * @param type Room type ("Single", "Double", "Suite")
     * @return Specialized Room instance
     */
    public static Room create(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        return switch (type.trim().toLowerCase()) {
            case "single" -> new SingleRoom();
            case "double" -> new DoubleRoom();
            case "suite" -> new SuiteRoom();
            default -> throw new IllegalArgumentException("Unknown room type: " + type);
        };
    }

    /**
     * Factory method creating and populating the appropriate Room subclass.
     */
    public static Room create(int id, String roomNumber, String type, BigDecimal basePrice, String status, String description) {
        Room room = create(type);
        room.setId(id);
        room.setRoomNumber(roomNumber);
        room.setType(type);
        room.setBasePrice(basePrice);
        room.setStatus(status != null ? status : "Available");
        room.setDescription(description);
        return room;
    }

    // --- Getters & Setters (Encapsulation) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        if (roomNumber != null && roomNumber.length() > 10) {
            throw new IllegalArgumentException("Room number cannot exceed 10 characters");
        }
        this.roomNumber = roomNumber != null ? roomNumber.trim() : null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        if (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Base price must be greater than zero");
        }
        this.basePrice = basePrice != null ? basePrice.setScale(2, RoundingMode.HALF_UP) : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("Description cannot exceed 255 characters");
        }
        this.description = description;
    }

    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(this.status);
    }

    public boolean isMaintenance() {
        return "Maintenance".equalsIgnoreCase(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id && Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", type='" + type + '\'' +
                ", basePrice=" + basePrice +
                ", nightlyRate=" + getNightlyRate() +
                ", capacity=" + getCapacity() +
                ", status='" + status + '\'' +
                '}';
    }
}
