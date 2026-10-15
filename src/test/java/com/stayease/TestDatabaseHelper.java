package com.stayease;

import com.stayease.util.DBConnection;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Helper to prepare and reset the test database schema and seed data for automated tests.
 */
public class TestDatabaseHelper {

    public static void initializeTestDatabase() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop tables if existing
            stmt.execute("DROP TABLE IF EXISTS reservations");
            stmt.execute("DROP TABLE IF EXISTS rooms");
            stmt.execute("DROP TABLE IF EXISTS users");

            // Create users table
            stmt.execute("""
                CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    full_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    phone VARCHAR(20),
                    password_hash VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL DEFAULT 'REGULAR'
                )
            """);

            // Create rooms table
            stmt.execute("""
                CREATE TABLE rooms (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    room_number VARCHAR(10) NOT NULL UNIQUE,
                    type VARCHAR(20) NOT NULL,
                    base_price DECIMAL(10, 2) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'Available',
                    description VARCHAR(255)
                )
            """);

            // Create reservations table
            stmt.execute("""
                CREATE TABLE reservations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    room_id INT NOT NULL,
                    check_in DATE NOT NULL,
                    check_out DATE NOT NULL,
                    guests INT NOT NULL,
                    total_price DECIMAL(10, 2) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
                )
            """);

            // Insert initial seed users
            // Password hash is for "Password@123"
            String hash = "$2a$12$g3GFDrvPsj7lEMCmVfjw8.HLVnS2GuIlJ6vDfpcod5si9rMMSovU2";
            stmt.execute(String.format("""
                INSERT INTO users (id, full_name, email, phone, password_hash, role) VALUES
                (1, 'Kamal Perera', 'admin@stayease.com', '+94771234567', '%s', 'ADMIN'),
                (2, 'Nimal Silva', 'nimal@gmail.com', '+94719876543', '%s', 'REGULAR'),
                (3, 'Sunil Fernando', 'sunil@gmail.com', '+94705556677', '%s', 'PREMIUM')
            """, hash, hash, hash));

            // Insert 7 rooms
            stmt.execute("""
                INSERT INTO rooms (id, room_number, type, base_price, status, description) VALUES
                (1, '101', 'Single', 60.00, 'Available', 'Cozy single room with city view'),
                (2, '102', 'Single', 65.00, 'Available', 'Quiet single room facing courtyard'),
                (3, '201', 'Double', 90.00, 'Available', 'Spacious double room with queen bed'),
                (4, '202', 'Double', 95.00, 'Available', 'Modern double room with balcony'),
                (5, '203', 'Double', 85.00, 'Maintenance', 'Double room undergoing AC repair'),
                (6, '301', 'Suite', 180.00, 'Available', 'Executive luxury suite with skyline view'),
                (7, '302', 'Suite', 200.00, 'Available', 'Presidential suite with jacuzzi')
            """);

            // Insert initial seed reservations
            stmt.execute("""
                INSERT INTO reservations (id, user_id, room_id, check_in, check_out, guests, total_price, status) VALUES
                (1, 2, 3, '2026-11-01', '2026-11-03', 2, 198.00, 'CONFIRMED'),
                (2, 3, 6, '2026-11-10', '2026-11-13', 3, 661.50, 'CONFIRMED')
            """);
        }
    }
}
