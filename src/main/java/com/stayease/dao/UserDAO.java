package com.stayease.dao;

import com.stayease.model.User;
import com.stayease.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User entity.
 * Handles database CRUD operations using PreparedStatements.
 *
 * Owned by Member 2 (Epic 2: Guest and Reservation Management).
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Inserts a new user record.
     *
     * @param user The User instance to insert
     * @return Generated user ID, or -1 if failed
     * @throws SQLException if a database error occurs (e.g., duplicate email)
     */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, phone, password_hash, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole() != null ? user.getRole() : "REGULAR");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        user.setId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Finds a user by ID.
     *
     * @param id User ID
     * @return User subclass instance, or null if not found
     */
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by id: " + id, e);
        }
        return null;
    }

    /**
     * Finds a user by email address (case-insensitive).
     *
     * @param email Email to search
     * @return User subclass instance, or null if not found
     */
    public User findByEmail(String email) {
        if (email == null) return null;
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by email: " + email, e);
        }
        return null;
    }

    /**
     * Retrieves all users.
     *
     * @return List of all User instances
     */
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error listing all users", e);
        }
        return list;
    }

    /**
     * Searches users by partial name, partial email, or exact ID.
     *
     * @param keyword Search term
     * @return List of matching User instances
     */
    public List<User> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        List<User> list = new ArrayList<>();
        String trimmed = keyword.trim();
        String pattern = "%" + trimmed + "%";

        Integer idVal = null;
        try {
            idVal = Integer.parseInt(trimmed);
        } catch (NumberFormatException ignored) {
            // Not a number, will search name and email
        }

        String sql;
        if (idVal != null) {
            sql = "SELECT * FROM users WHERE id = ? OR LOWER(full_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) ORDER BY id ASC";
        } else {
            sql = "SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?) ORDER BY id ASC";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (idVal != null) {
                ps.setInt(1, idVal);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
            } else {
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching users with keyword: " + keyword, e);
        }
        return list;
    }

    /**
     * Updates an existing user's details.
     * If updatePassword is true, updates the password_hash column as well.
     *
     * @param user User instance
     * @param updatePassword Whether to update the password hash
     * @return true if updated, false otherwise
     * @throws SQLException on database error
     */
    public boolean update(User user, boolean updatePassword) throws SQLException {
        String sql;
        if (updatePassword) {
            sql = "UPDATE users SET full_name = ?, phone = ?, role = ?, password_hash = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET full_name = ?, phone = ?, role = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getRole());

            if (updatePassword) {
                ps.setString(4, user.getPasswordHash());
                ps.setInt(5, user.getId());
            } else {
                ps.setInt(4, user.getId());
            }

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user by ID. Associated reservations cascade automatically.
     *
     * @param id User ID to delete
     * @return true if deleted, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user with id: " + id, e);
            return false;
        }
    }

    /**
     * Maps ResultSet row to polymorphic User subclass.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String passwordHash = rs.getString("password_hash");
        String role = rs.getString("role");

        return User.create(id, fullName, email, phone, passwordHash, role);
    }
}
