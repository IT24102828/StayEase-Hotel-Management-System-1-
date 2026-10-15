package com.stayease.model;

import com.stayease.TestDatabaseHelper;
import com.stayease.dao.RoomDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite for Epic 1: Room Management.
 * Verifies OOP models, polymorphic rate calculations, and RoomDAO CRUD.
 * Covers requirements R-01 to R-06 and test cases TC-R-01 through TC-R-09.
 */
public class RoomTest {

    private RoomDAO roomDAO;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.initializeTestDatabase();
        roomDAO = new RoomDAO();
    }

    @Test
    @DisplayName("TC-R-09: Polymorphic pricing for Double and Suite rooms")
    public void testPolymorphicPricing() {
        // Double: 90 * 1.10 = 99.00
        Room doubleRoom = Room.create(0, "201", "Double", new BigDecimal("90.00"), "Available", "Test Double");
        assertEquals(new BigDecimal("99.00"), doubleRoom.getNightlyRate());
        assertEquals(2, doubleRoom.getCapacity());

        // Suite: 180 * 1.25 + 20.00 = 225.00 + 20.00 = 245.00
        Room suiteRoom = Room.create(0, "301", "Suite", new BigDecimal("180.00"), "Available", "Test Suite");
        assertEquals(new BigDecimal("245.00"), suiteRoom.getNightlyRate());
        assertEquals(4, suiteRoom.getCapacity());

        // Single: 60.00 base -> 60.00
        Room singleRoom = Room.create(0, "101", "Single", new BigDecimal("60.00"), "Available", "Test Single");
        assertEquals(new BigDecimal("60.00"), singleRoom.getNightlyRate());
        assertEquals(1, singleRoom.getCapacity());
    }

    @Test
    @DisplayName("TC-R-01: Admin adds room 401, Double, 95, Available")
    public void testAddRoomValid() throws Exception {
        Room newRoom = Room.create(0, "401", "Double", new BigDecimal("95.00"), "Available", "Brand new room");
        int generatedId = roomDAO.insert(newRoom);
        assertTrue(generatedId > 0);

        Room retrieved = roomDAO.findByRoomNumber("401");
        assertNotNull(retrieved);
        assertEquals("Double", retrieved.getType());
        assertEquals(new BigDecimal("95.00"), retrieved.getBasePrice());
        assertEquals(new BigDecimal("104.50"), retrieved.getNightlyRate()); // 95 * 1.10
        assertEquals("Available", retrieved.getStatus());
    }

    @Test
    @DisplayName("TC-R-02: Add a room with number 101 again (Duplicate Rejection)")
    public void testAddDuplicateRoomNumber() {
        Room duplicateRoom = Room.create(0, "101", "Single", new BigDecimal("75.00"), "Available", "Duplicate");
        assertThrows(SQLException.class, () -> {
            roomDAO.insert(duplicateRoom);
        });
    }

    @Test
    @DisplayName("TC-R-03: Add room with price 0 or negative price rejected")
    public void testInvalidPriceRejection() {
        Room roomZero = Room.create("Single");
        assertThrows(IllegalArgumentException.class, () -> {
            roomZero.setBasePrice(BigDecimal.ZERO);
        });

        Room roomNegative = Room.create("Double");
        assertThrows(IllegalArgumentException.class, () -> {
            roomNegative.setBasePrice(new BigDecimal("-10.00"));
        });
    }

    @Test
    @DisplayName("TC-R-05: Search 'Suite' returns only Suite rooms")
    public void testSearchSuite() {
        List<Room> suites = roomDAO.search("Suite");
        assertFalse(suites.isEmpty());
        for (Room r : suites) {
            assertEquals("Suite", r.getType());
        }
    }

    @Test
    @DisplayName("TC-R-06: Search 'zzz' returns empty list (meaningful empty state)")
    public void testSearchNonExistent() {
        List<Room> result = roomDAO.search("zzz");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC-R-07: Edit room 102 price to 70 and verify updated rate")
    public void testUpdateRoomPrice() throws Exception {
        Room room102 = roomDAO.findByRoomNumber("102");
        assertNotNull(room102);
        room102.setBasePrice(new BigDecimal("70.00"));

        boolean updated = roomDAO.update(room102);
        assertTrue(updated);

        Room reloaded = roomDAO.findById(room102.getId());
        assertEquals(new BigDecimal("70.00"), reloaded.getBasePrice());
        assertEquals(new BigDecimal("70.00"), reloaded.getNightlyRate());
    }

    @Test
    @DisplayName("TC-R-08: Delete room 302 and confirm removal")
    public void testDeleteRoom() {
        Room room302 = roomDAO.findByRoomNumber("302");
        assertNotNull(room302);

        boolean deleted = roomDAO.delete(room302.getId());
        assertTrue(deleted);

        Room checkAfter = roomDAO.findByRoomNumber("302");
        assertNull(checkAfter);
    }
}
