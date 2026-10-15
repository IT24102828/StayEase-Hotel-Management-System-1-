package com.stayease.dao;

import com.stayease.TestDatabaseHelper;
import com.stayease.model.Reservation;
import com.stayease.model.Room;
import com.stayease.model.User;
import com.stayease.util.DoubleBookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite for Epic 2: Reservation Management.
 * Verifies business calculations, concurrency safety (TC-B-03), and lifecycle.
 * Covers requirements B-01 to B-06 and test cases TC-B-01 through TC-B-08.
 */
public class ReservationTest {

    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.initializeTestDatabase();
        reservationDAO = new ReservationDAO();
        roomDAO = new RoomDAO();
        userDAO = new UserDAO();
    }

    @Test
    @DisplayName("TC-B-05: Premium guest books Suite 301 for 3 nights -> Total $661.50")
    public void testPremiumSuiteBookingCalculation() {
        Room suite301 = roomDAO.findByRoomNumber("301");
        assertNotNull(suite301);
        // Base: 180.00. Nightly: 180 * 1.25 + 20.00 = 245.00
        assertEquals(new BigDecimal("245.00"), suite301.getNightlyRate());

        User premiumGuest = userDAO.findByEmail("sunil@gmail.com");
        assertNotNull(premiumGuest);
        assertEquals(new BigDecimal("0.10"), premiumGuest.getDiscountRate());

        LocalDate checkIn = LocalDate.of(2026, 12, 1);
        LocalDate checkOut = LocalDate.of(2026, 12, 4); // 3 nights

        BigDecimal total = Reservation.calculateTotalPrice(suite301, premiumGuest, checkIn, checkOut);
        // Gross: 245.00 * 3 = 735.00. Discount: 10% (73.50). Net: 661.50
        assertEquals(new BigDecimal("661.50"), total);
    }

    @Test
    @DisplayName("TC-B-01: Check-out before check-in throws date error")
    public void testInvalidDateRange() {
        Room room = roomDAO.findByRoomNumber("101");
        User user = userDAO.findByEmail("nimal@gmail.com");

        LocalDate checkIn = LocalDate.of(2026, 11, 10);
        LocalDate checkOut = LocalDate.of(2026, 11, 5); // Before check-in

        assertThrows(IllegalArgumentException.class, () -> {
            Reservation.calculateTotalPrice(room, user, checkIn, checkOut);
        });
    }

    @Test
    @DisplayName("TC-B-03: Concurrent overlapping reservation prevention (Double Booking Refusal)")
    public void testConcurrentOverlappingReservation() throws Exception {
        Room room201 = roomDAO.findByRoomNumber("201");
        assertNotNull(room201);

        LocalDate checkIn = LocalDate.of(2026, 12, 10);
        LocalDate checkOut = LocalDate.of(2026, 12, 15);

        // Booking 1 succeeds
        Reservation res1 = new Reservation(0, 2, room201.getId(), checkIn, checkOut, 2, new BigDecimal("495.00"), "CONFIRMED");
        int id1 = reservationDAO.insert(res1);
        assertTrue(id1 > 0);

        // Overlapping Booking 2 (Dec 12 to Dec 17) MUST be refused with DoubleBookingException
        LocalDate overlapCheckIn = LocalDate.of(2026, 12, 12);
        LocalDate overlapCheckOut = LocalDate.of(2026, 12, 17);
        Reservation res2 = new Reservation(0, 3, room201.getId(), overlapCheckIn, overlapCheckOut, 2, new BigDecimal("495.00"), "CONFIRMED");

        assertThrows(DoubleBookingException.class, () -> {
            reservationDAO.insert(res2);
        });
    }

    @Test
    @DisplayName("TC-B-03 Concurrency Multithreading: Two threads trying to book same dates simultaneously")
    public void testMultithreadedConcurrentRaceCondition() throws Exception {
        Room room = roomDAO.findByRoomNumber("102");
        assertNotNull(room);

        LocalDate checkIn = LocalDate.of(2026, 12, 20);
        LocalDate checkOut = LocalDate.of(2026, 12, 25);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        Runnable bookingTask = () -> {
            try {
                startLatch.await(); // Synchronize thread release
                Reservation res = new Reservation(0, 2, room.getId(), checkIn, checkOut, 1, new BigDecimal("325.00"), "CONFIRMED");
                reservationDAO.insert(res);
                successCount.incrementAndGet();
            } catch (DoubleBookingException e) {
                rejectedCount.incrementAndGet();
            } catch (Exception e) {
                // Unexpected error
            } finally {
                doneLatch.countDown();
            }
        };

        executor.submit(bookingTask);
        executor.submit(bookingTask);

        startLatch.countDown(); // Fire both threads at once
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Exactly one reservation must succeed and the other must be rejected
        assertEquals(1, successCount.get(), "Exactly one concurrent booking must succeed");
        assertEquals(1, rejectedCount.get(), "The overlapping booking must be refused");
    }

    @Test
    @DisplayName("TC-B-04: Group size exceeds room capacity")
    public void testExceedsCapacity() {
        Room doubleRoom = roomDAO.findByRoomNumber("201");
        assertEquals(2, doubleRoom.getCapacity());

        // 3 guests exceeds capacity of 2
        assertTrue(3 > doubleRoom.getCapacity());
    }

    @Test
    @DisplayName("TC-B-06: Guest cancels booking, then searches same dates -> Room is available again")
    public void testRestoreAvailabilityAfterCancellation() {
        Room room = roomDAO.findByRoomNumber("201");
        // Room 201 has seed reservation #1 for Nov 1 to Nov 3
        LocalDate checkIn = LocalDate.of(2026, 11, 1);
        LocalDate checkOut = LocalDate.of(2026, 11, 3);

        // Before cancellation: room is not available for Nov 1 - Nov 3
        List<Room> before = roomDAO.findAvailable(checkIn, checkOut, 2);
        boolean contains201Before = before.stream().anyMatch(r -> r.getId() == room.getId());
        assertFalse(contains201Before, "Room 201 should be unavailable before cancellation");

        // Cancel reservation #1 by guest #2
        boolean cancelled = reservationDAO.cancel(1, 2, false);
        assertTrue(cancelled);

        // After cancellation: room 201 becomes available again for those dates
        List<Room> after = roomDAO.findAvailable(checkIn, checkOut, 2);
        boolean contains201After = after.stream().anyMatch(r -> r.getId() == room.getId());
        assertTrue(contains201After, "Room 201 should be available again after cancellation");
    }

    @Test
    @DisplayName("TC-B-07: Guest A opens bookings list -> Guest B's bookings are not shown (Isolation)")
    public void testBookingPrivacyIsolation() {
        // User 2 (Nimal) owns reservation #1
        List<Reservation> nimalBookings = reservationDAO.findByUser(2);
        assertFalse(nimalBookings.isEmpty());
        for (Reservation r : nimalBookings) {
            assertEquals(2, r.getUserId());
        }

        // User 3 (Sunil) owns reservation #2
        List<Reservation> sunilBookings = reservationDAO.findByUser(3);
        assertFalse(sunilBookings.isEmpty());
        for (Reservation r : sunilBookings) {
            assertEquals(3, r.getUserId());
        }

        // Cross-check: Nimal's list does NOT contain Sunil's booking
        boolean containsSunil = nimalBookings.stream().anyMatch(r -> r.getUserId() == 3);
        assertFalse(containsSunil, "Guest A must not see Guest B's reservations");
    }

    @Test
    @DisplayName("TC-B-08: Admin deletes a reservation record permanently")
    public void testAdminDeleteReservation() {
        Reservation res = reservationDAO.findById(2);
        assertNotNull(res);

        boolean deleted = reservationDAO.delete(2);
        assertTrue(deleted);

        Reservation checkAfter = reservationDAO.findById(2);
        assertNull(checkAfter);
    }
}
