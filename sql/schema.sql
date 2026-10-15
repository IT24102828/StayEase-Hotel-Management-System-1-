-- =====================================================================
-- SE1020 - Hotel Room Reservation System
-- Database Schema & Sample Demonstration Data
-- Compatible with MySQL 8.0+
-- Database: stayease_db
-- =====================================================================

CREATE DATABASE IF NOT EXISTS stayease_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE stayease_db;

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- 1. USERS TABLE
-- Roles: ADMIN, REGULAR, PREMIUM
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'REGULAR', 'PREMIUM'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. ROOMS TABLE
-- Types: Single, Double, Suite
-- Statuses: Available, Maintenance
-- ---------------------------------------------------------------------
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_room_type CHECK (type IN ('Single', 'Double', 'Suite')),
    CONSTRAINT chk_room_status CHECK (status IN ('Available', 'Maintenance')),
    CONSTRAINT chk_room_price CHECK (base_price > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. RESERVATIONS TABLE
-- Statuses: CONFIRMED, CANCELLED
-- ---------------------------------------------------------------------
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guests INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT chk_res_status CHECK (status IN ('CONFIRMED', 'CANCELLED')),
    CONSTRAINT chk_res_guests CHECK (guests >= 1 AND guests <= 10),
    CONSTRAINT chk_res_dates CHECK (check_out > check_in)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for rapid queries and date overlap checks
CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_room ON reservations(room_id);
CREATE INDEX idx_reservations_dates ON reservations(room_id, check_in, check_out, status);
CREATE INDEX idx_rooms_search ON rooms(type, status, base_price);

-- =====================================================================
-- DEMONSTRATION / INITIAL SAMPLE DATA
-- Password for all seed users is: Password@123
-- BCrypt Hash: $2a$12$g3GFDrvPsj7lEMCmVfjw8.HLVnS2GuIlJ6vDfpcod5si9rMMSovU2
-- =====================================================================

-- Users (1 Admin, 1 Regular Guest, 1 Premium Guest)
INSERT INTO users (id, full_name, email, phone, password_hash, role) VALUES
(1, 'Kamal Perera', 'admin@stayease.com', '+94771234567', '$2a$12$g3GFDrvPsj7lEMCmVfjw8.HLVnS2GuIlJ6vDfpcod5si9rMMSovU2', 'ADMIN'),
(2, 'Nimal Silva', 'nimal@gmail.com', '+94719876543', '$2a$12$g3GFDrvPsj7lEMCmVfjw8.HLVnS2GuIlJ6vDfpcod5si9rMMSovU2', 'REGULAR'),
(3, 'Sunil Fernando', 'sunil@gmail.com', '+94705556677', '$2a$12$g3GFDrvPsj7lEMCmVfjw8.HLVnS2GuIlJ6vDfpcod5si9rMMSovU2', 'PREMIUM');

-- Rooms (7 rooms covering Single, Double, Suite, and Maintenance)
INSERT INTO rooms (id, room_number, type, base_price, status, description) VALUES
(1, '101', 'Single', 60.00, 'Available', 'Cozy single room with high-speed Wi-Fi and city view'),
(2, '102', 'Single', 65.00, 'Available', 'Quiet single room facing the tranquil hotel courtyard'),
(3, '201', 'Double', 90.00, 'Available', 'Spacious double room with queen bed and private balcony'),
(4, '202', 'Double', 95.00, 'Available', 'Modern double room with smart entertainment and work desk'),
(5, '203', 'Double', 85.00, 'Maintenance', 'Double room currently undergoing seasonal AC duct maintenance'),
(6, '301', 'Suite', 180.00, 'Available', 'Executive luxury suite with panoramic skyline view and lounge'),
(7, '302', 'Suite', 200.00, 'Available', 'Presidential penthouse suite with private jacuzzi and minibar');

-- Reservations (2 active bookings)
-- Note: Room 201 Double: Nightly rate = 90 * 1.10 = $99.00. 2 nights (Nov 1 to Nov 3) = $198.00 (Regular guest = 0% discount).
-- Note: Room 301 Suite: Nightly rate = 180 * 1.25 + 20 = $245.00. 3 nights (Nov 10 to Nov 13) = $735.00. Premium guest = 10% off -> $661.50 (TC-B-05).
INSERT INTO reservations (id, user_id, room_id, check_in, check_out, guests, total_price, status) VALUES
(1, 2, 3, '2026-11-01', '2026-11-03', 2, 198.00, 'CONFIRMED'),
(2, 3, 6, '2026-11-10', '2026-11-13', 3, 661.50, 'CONFIRMED');

-- Set auto-increment anchors
ALTER TABLE users AUTO_INCREMENT = 4;
ALTER TABLE rooms AUTO_INCREMENT = 8;
ALTER TABLE reservations AUTO_INCREMENT = 3;
