package com.stayease.dao;

import com.stayease.model.Reservation;
import com.stayease.util.DBConnection;
import com.stayease.util.DoubleBookingException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Reservation entity.
 * Implements strict transaction control and row-locking to prevent concurrent double-booking.
 *
 * Owned by Member 2 (Epic 2: Guest and Reservation Management).
 */
public class ReservationDAO {

    private static final Logger LOGGER = Logger.getLogger(ReservationDAO.class.getName());

    /**
     * Atomically locks the room and inserts a new confirmed reservation inside a transaction.
     * Prevents race conditions and duplicate bookings under concurrent load (TC-B-03).
     *
     * @param res The Reservation to book
     * @return Generated reservation ID
     * @throws DoubleBookingException if the room is taken or under maintenance
     * @throws SQLException on database transaction error
     */
    public int insert(Reservation res) throws DoubleBookingException, SQLException {
        String lockRoomSql = "SELECT status FROM rooms WHERE id = ? FOR UPDATE";
        String checkOverlapSql = "SELECT id FROM reservations " +
                "WHERE room_id = ? AND status = 'CONFIRMED' " +
                "AND check_in < ? AND check_out > ?";
        String insertSql = "INSERT INTO reservations (user_id, room_id, check_in, check_out, guests, total_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Lock the room row and verify status
            try (PreparedStatement psLock = conn.prepareStatement(lockRoomSql)) {
                psLock.setInt(1, res.getRoomId());
                try (ResultSet rsRoom = psLock.executeQuery()) {
                    if (!rsRoom.next()) {
                        conn.rollback();
                        throw new SQLException("Selected room not found: " + res.getRoomId());
                    }
                    String roomStatus = rsRoom.getString("status");
                    if (!"Available".equalsIgnoreCase(roomStatus)) {
                        conn.rollback();
                        throw new DoubleBookingException("Room is currently under maintenance and cannot be reserved.");
                    }
                }
            }

            // Step 2: Check for existing overlapping CONFIRMED reservations with row lock
            try (PreparedStatement psOverlap = conn.prepareStatement(checkOverlapSql)) {
                psOverlap.setInt(1, res.getRoomId());
                psOverlap.setDate(2, java.sql.Date.valueOf(res.getCheckOut()));
                psOverlap.setDate(3, java.sql.Date.valueOf(res.getCheckIn()));

                try (ResultSet rsOverlap = psOverlap.executeQuery()) {
                    if (rsOverlap.next()) {
                        conn.rollback();
                        throw new DoubleBookingException("The selected room has already been reserved for these dates. Please choose different dates or another room.");
                    }
                }
            }

            // Step 3: Insert the reservation
            int generatedId = -1;
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, res.getUserId());
                psInsert.setInt(2, res.getRoomId());
                psInsert.setDate(3, java.sql.Date.valueOf(res.getCheckIn()));
                psInsert.setDate(4, java.sql.Date.valueOf(res.getCheckOut()));
                psInsert.setInt(5, res.getGuests());
                psInsert.setBigDecimal(6, res.getTotalPrice());
                psInsert.setString(7, res.getStatus() != null ? res.getStatus() : "CONFIRMED");

                int affected = psInsert.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rsKeys = psInsert.getGeneratedKeys()) {
                        if (rsKeys.next()) {
                            generatedId = rsKeys.getInt(1);
                            res.setId(generatedId);
                        }
                    }
                }
            }

            // Commit transaction atomically
            conn.commit();
            return generatedId;

        } catch (SQLException | DoubleBookingException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback transaction", rollbackEx);
                }
            }
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.WARNING, "Error restoring connection auto-commit", closeEx);
                }
            }
        }
    }

    /**
     * Finds a reservation by ID with joined User and Room details.
     *
     * @param id Reservation ID
     * @return Populated Reservation, or null if not found
     */
    public Reservation findById(int id) {
        String sql = "SELECT res.*, u.full_name AS guest_name, u.email AS guest_email, " +
                "r.room_number, r.type AS room_type, r.base_price " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "WHERE res.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservation by id: " + id, e);
        }
        return null;
    }

    /**
     * Retrieves all reservations made by a specific user.
     *
     * @param userId Guest user ID
     * @return List of user reservations
     */
    public List<Reservation> findByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT res.*, u.full_name AS guest_name, u.email AS guest_email, " +
                "r.room_number, r.type AS room_type, r.base_price " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "WHERE res.user_id = ? " +
                "ORDER BY res.check_in DESC, res.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservations for user: " + userId, e);
        }
        return list;
    }

    /**
     * Retrieves all reservations across all guests (Admin view).
     *
     * @return Full list of reservations
     */
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT res.*, u.full_name AS guest_name, u.email AS guest_email, " +
                "r.room_number, r.type AS room_type, r.base_price " +
                "FROM reservations res " +
                "JOIN users u ON res.user_id = u.id " +
                "JOIN rooms r ON res.room_id = r.id " +
                "ORDER BY res.check_in DESC, res.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all reservations", e);
        }
        return list;
    }

    /**
     * Cancels a reservation by changing its status to CANCELLED.
     * Enforces ownership authorization: guests can only cancel their own booking.
     *
     * @param id Reservation ID
     * @param userId Requesting user ID
     * @param isAdmin Whether the requesting user is an Admin
     * @return true if successfully cancelled, false if unauthorized or not found
     */
    public boolean cancel(int id, int userId, boolean isAdmin) {
        Reservation res = findById(id);
        if (res == null) {
            return false;
        }

        // Authorization check: guest can only cancel own booking
        if (!isAdmin && res.getUserId() != userId) {
            LOGGER.warning("Unauthorized cancellation attempt: User " + userId + " tried to cancel reservation " + id);
            return false;
        }

        String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error cancelling reservation: " + id, e);
            return false;
        }
    }

    /**
     * Permanently deletes a reservation record from the database.
     * Restricted to administrators.
     *
     * @param id Reservation ID to permanently delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error permanently deleting reservation: " + id, e);
            return false;
        }
    }

    /**
     * Maps ResultSet row to Reservation domain object.
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int roomId = rs.getInt("room_id");
        LocalDate checkIn = rs.getDate("check_in").toLocalDate();
        LocalDate checkOut = rs.getDate("check_out").toLocalDate();
        int guests = rs.getInt("guests");
        BigDecimal totalPrice = rs.getBigDecimal("total_price");
        String status = rs.getString("status");

        Reservation res = new Reservation(id, userId, roomId, checkIn, checkOut, guests, totalPrice, status);

        // Map presentation attributes from joins
        try {
            res.setGuestName(rs.getString("guest_name"));
            res.setGuestEmail(rs.getString("guest_email"));
            res.setRoomNumber(rs.getString("room_number"));
            res.setRoomType(rs.getString("room_type"));
        } catch (SQLException ignored) {
            // Joins may not be present in raw queries
        }

        return res;
    }
}
