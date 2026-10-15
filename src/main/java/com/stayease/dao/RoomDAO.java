package com.stayease.dao;

import com.stayease.model.Room;
import com.stayease.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Room entity.
 * Handles database CRUD operations using PreparedStatements.
 *
 * Owned by Member 1 (Epic 1: Room Management).
 */
public class RoomDAO {

    private static final Logger LOGGER = Logger.getLogger(RoomDAO.class.getName());

    /**
     * Inserts a new room record into the database.
     *
     * @param room The Room instance to insert
     * @return Generated primary key ID, or -1 if failed
     * @throws SQLException if a database error occurs (e.g., duplicate room number)
     */
    public int insert(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, type, base_price, status, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType());
            ps.setBigDecimal(3, room.getBasePrice());
            ps.setString(4, room.getStatus() != null ? room.getStatus() : "Available");
            ps.setString(5, room.getDescription());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        room.setId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Finds a room by primary key.
     *
     * @param id Room ID
     * @return Specialized Room subclass instance, or null if not found
     */
    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding room by id: " + id, e);
        }
        return null;
    }

    /**
     * Finds a room by its unique room number.
     *
     * @param roomNumber Room number string
     * @return Specialized Room subclass instance, or null if not found
     */
    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding room by number: " + roomNumber, e);
        }
        return null;
    }

    /**
     * Retrieves all rooms in the system ordered by room number.
     *
     * @return List of Room instances
     */
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing all rooms", e);
        }
        return rooms;
    }

    /**
     * Searches rooms matching keyword against room number, type, or status.
     *
     * @param keyword Search query (null or blank returns all rooms)
     * @return Matching Room list
     */
    public List<Room> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        List<Room> rooms = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ? OR type LIKE ? OR status LIKE ? ORDER BY room_number ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching rooms with keyword: " + keyword, e);
        }
        return rooms;
    }

    /**
     * Updates an existing room record.
     *
     * @param room Room instance with updated values
     * @return true if updated, false otherwise
     * @throws SQLException if duplicate room number or database error
     */
    public boolean update(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, type = ?, base_price = ?, status = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType());
            ps.setBigDecimal(3, room.getBasePrice());
            ps.setString(4, room.getStatus());
            ps.setString(5, room.getDescription());
            ps.setInt(6, room.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a room by ID. Reservations with foreign key constraints will cascade or be removed.
     *
     * @param id Room ID to delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room with id: " + id, e);
            return false;
        }
    }

    /**
     * Finds available rooms for the given check-in/check-out dates and guest count.
     * Excludes rooms undergoing maintenance and rooms with overlapping CONFIRMED reservations.
     *
     * @param checkIn Stay check-in date
     * @param checkOut Stay check-out date
     * @param guests Required guest capacity
     * @return List of suitable Room instances
     */
    public List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut, int guests) {
        List<Room> result = new ArrayList<>();
        // Query rooms that are 'Available' and have NO overlapping CONFIRMED reservations:
        // Overlap condition: existing.check_in < requested.check_out AND existing.check_out > requested.check_in
        String sql = "SELECT * FROM rooms r WHERE r.status = 'Available' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM reservations res " +
                "    WHERE res.room_id = r.id " +
                "      AND res.status = 'CONFIRMED' " +
                "      AND res.check_in < ? " +
                "      AND res.check_out > ?" +
                ") ORDER BY r.room_number ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(checkOut));
            ps.setDate(2, java.sql.Date.valueOf(checkIn));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = mapResultSetToRoom(rs);
                    // Polymorphic capacity check
                    if (room.getCapacity() >= guests) {
                        result.add(room);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error querying available rooms", e);
        }
        return result;
    }

    /**
     * Maps database ResultSet row to appropriate polymorphic Room subclass instance.
     */
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String roomNumber = rs.getString("room_number");
        String type = rs.getString("type");
        BigDecimal basePrice = rs.getBigDecimal("base_price");
        String status = rs.getString("status");
        String description = rs.getString("description");

        return Room.create(id, roomNumber, type, basePrice, status, description);
    }
}
