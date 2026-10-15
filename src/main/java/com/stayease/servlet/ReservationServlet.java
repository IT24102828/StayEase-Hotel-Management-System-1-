package com.stayease.servlet;

import com.stayease.dao.ReservationDAO;
import com.stayease.dao.RoomDAO;
import com.stayease.model.Reservation;
import com.stayease.model.Room;
import com.stayease.model.User;
import com.stayease.util.DoubleBookingException;
import com.stayease.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller Servlet for Room Availability Search, Booking, and Reservation Management.
 *
 * Owned by Member 2 (Epic 2: Guest and Reservation Management).
 */
@WebServlet(name = "ReservationServlet", urlPatterns = {"/reservations"})
public class ReservationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReservationServlet.class.getName());

    private ReservationDAO reservationDAO;
    private RoomDAO roomDAO;

    @Override
    public void init() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
    }

    public void setReservationDAO(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    public void setRoomDAO(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtil.transferFlashMessages(request);
        String action = request.getParameter("action");
        if (action == null) {
            action = "search";
        }

        switch (action) {
            case "search" -> handleAvailabilitySearch(request, response);
            case "list" -> handleListReservations(request, response);
            default -> handleAvailabilitySearch(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "book" -> handleBooking(request, response);
            case "cancel" -> handleCancel(request, response);
            case "delete" -> handleDelete(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/reservations?action=search");
        }
    }

    /**
     * B-01: Searches available rooms for given date range and guest count.
     */
    private void handleAvailabilitySearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        // If no search parameters provided, render empty search page
        if (checkInStr == null && checkOutStr == null && guestsStr == null) {
            request.getRequestDispatcher("/WEB-INF/views/reservation/reservation-form.jsp").forward(request, response);
            return;
        }

        LocalDate checkIn;
        LocalDate checkOut;
        int guests;

        try {
            checkIn = LocalDate.parse(checkInStr != null ? checkInStr.trim() : "");
            checkOut = LocalDate.parse(checkOutStr != null ? checkOutStr.trim() : "");
        } catch (DateTimeParseException | NullPointerException e) {
            request.setAttribute("errorMessage", "Please provide valid check-in and check-out dates.");
            request.setAttribute("inputCheckIn", checkInStr);
            request.setAttribute("inputCheckOut", checkOutStr);
            request.setAttribute("inputGuests", guestsStr);
            request.getRequestDispatcher("/WEB-INF/views/reservation/reservation-form.jsp").forward(request, response);
            return;
        }

        try {
            guests = Integer.parseInt(guestsStr != null ? guestsStr.trim() : "1");
            if (guests < 1 || guests > 10) {
                request.setAttribute("errorMessage", "Guest count must be between 1 and 10.");
                forwardSearch(request, response, checkInStr, checkOutStr, guestsStr);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Please enter a valid number of guests (1–10).");
            forwardSearch(request, response, checkInStr, checkOutStr, guestsStr);
            return;
        }

        // Validate: Check-in cannot be in the past (TC-B-02)
        if (checkIn.isBefore(LocalDate.now())) {
            request.setAttribute("errorMessage", "Check-in can't be in the past.");
            forwardSearch(request, response, checkInStr, checkOutStr, guestsStr);
            return;
        }

        // Validate: Check-out must be after check-in (TC-B-01)
        if (!checkOut.isAfter(checkIn)) {
            request.setAttribute("errorMessage", "Check-out date must be after check-in date.");
            forwardSearch(request, response, checkInStr, checkOutStr, guestsStr);
            return;
        }

        // Validate: Maximum stay length of 30 nights
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > 30) {
            request.setAttribute("errorMessage", "Maximum allowable stay is 30 nights.");
            forwardSearch(request, response, checkInStr, checkOutStr, guestsStr);
            return;
        }

        // Find available rooms matching capacity and dates
        List<Room> availableRooms = roomDAO.findAvailable(checkIn, checkOut, guests);

        // Precalculate estimated total prices based on logged-in user's polymorphic discount
        User loggedUser = SessionUtil.getLoggedInUser(request);
        Map<Integer, BigDecimal> priceMap = new HashMap<>();
        for (Room r : availableRooms) {
            BigDecimal total;
            if (loggedUser != null) {
                total = Reservation.calculateTotalPrice(r, loggedUser, checkIn, checkOut);
            } else {
                // If not logged in, show regular price (0% discount)
                BigDecimal gross = r.getNightlyRate().multiply(BigDecimal.valueOf(nights));
                total = gross.setScale(2, java.math.RoundingMode.HALF_UP);
            }
            priceMap.put(r.getId(), total);
        }

        request.setAttribute("checkIn", checkIn);
        request.setAttribute("checkOut", checkOut);
        request.setAttribute("guests", guests);
        request.setAttribute("nights", nights);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("priceMap", priceMap);
        request.setAttribute("searched", true);

        request.getRequestDispatcher("/WEB-INF/views/reservation/reservation-form.jsp").forward(request, response);
    }

    private void forwardSearch(HttpServletRequest request, HttpServletResponse response,
                               String checkIn, String checkOut, String guests)
            throws ServletException, IOException {
        request.setAttribute("inputCheckIn", checkIn);
        request.setAttribute("inputCheckOut", checkOut);
        request.setAttribute("inputGuests", guests);
        request.getRequestDispatcher("/WEB-INF/views/reservation/reservation-form.jsp").forward(request, response);
    }

    /**
     * B-02: Books a selected room. Handles server-side validation and transaction locking.
     */
    private void handleBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null) {
            SessionUtil.setFlashError(request, "Please log in to complete your reservation.");
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        String roomIdStr = request.getParameter("roomId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        int roomId;
        LocalDate checkIn;
        LocalDate checkOut;
        int guests;

        try {
            roomId = Integer.parseInt(roomIdStr != null ? roomIdStr.trim() : "");
            checkIn = LocalDate.parse(checkInStr != null ? checkInStr.trim() : "");
            checkOut = LocalDate.parse(checkOutStr != null ? checkOutStr.trim() : "");
            guests = Integer.parseInt(guestsStr != null ? guestsStr.trim() : "1");
        } catch (Exception e) {
            SessionUtil.setFlashError(request, "Invalid booking parameters provided.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            SessionUtil.setFlashError(request, "Check-in can't be in the past.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            SessionUtil.setFlashError(request, "Check-out date must be after check-in date.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > 30) {
            SessionUtil.setFlashError(request, "Maximum stay is 30 nights.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        Room room = roomDAO.findById(roomId);
        if (room == null) {
            SessionUtil.setFlashError(request, "Selected room was not found.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        if (!room.isAvailable()) {
            SessionUtil.setFlashError(request, "This room is currently under maintenance.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        // TC-B-04: Room capacity validation
        if (guests > room.getCapacity()) {
            SessionUtil.setFlashError(request, "This " + room.getType() + " room can accommodate a maximum of "
                    + room.getCapacity() + " guests (requested: " + guests + ").");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            return;
        }

        // Calculate total price securely on backend using polymorphic discount (B-06 / TC-B-05)
        BigDecimal calculatedTotal = Reservation.calculateTotalPrice(room, loggedUser, checkIn, checkOut);

        Reservation res = new Reservation();
        res.setUserId(loggedUser.getId());
        res.setRoomId(room.getId());
        res.setCheckIn(checkIn);
        res.setCheckOut(checkOut);
        res.setGuests(guests);
        res.setTotalPrice(calculatedTotal);
        res.setStatus("CONFIRMED");

        try {
            // Atomic insertion with row lock (TC-B-03)
            int bookingId = reservationDAO.insert(res);
            SessionUtil.setFlashSuccess(request, "Reservation confirmed! Room " + room.getRoomNumber() +
                    " booked for " + nights + " night(s). Total: $" + calculatedTotal);
            response.sendRedirect(request.getContextPath() + "/reservations?action=list");
        } catch (DoubleBookingException e) {
            // Concurrency or date conflict error
            SessionUtil.setFlashError(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reservations?action=search&checkIn=" + checkIn + "&checkOut=" + checkOut + "&guests=" + guests);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error creating reservation", e);
            SessionUtil.setFlashError(request, "Failed to confirm reservation due to a database error. Please try again.");
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
        }
    }

    /**
     * B-03: Lists reservations. Guests see own; Admins see all (TC-B-07).
     */
    private void handleListReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        List<Reservation> reservations;
        if (loggedUser.isAdmin()) {
            reservations = reservationDAO.findAll();
        } else {
            reservations = reservationDAO.findByUser(loggedUser.getId());
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("isAdminView", loggedUser.isAdmin());
        request.getRequestDispatcher("/WEB-INF/views/reservation/reservation-list.jsp").forward(request, response);
    }

    /**
     * B-04: Cancels reservation. (TC-B-04 / TC-B-06)
     */
    private void handleCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        String idStr = request.getParameter("id");
        try {
            int id = Integer.parseInt(idStr != null ? idStr.trim() : "");
            boolean cancelled = reservationDAO.cancel(id, loggedUser.getId(), loggedUser.isAdmin());
            if (cancelled) {
                SessionUtil.setFlashSuccess(request, "Reservation cancelled.");
            } else {
                SessionUtil.setFlashError(request, "Unable to cancel reservation (either not found or unauthorized).");
            }
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(request, "Invalid reservation ID.");
        }
        response.sendRedirect(request.getContextPath() + "/reservations?action=list");
    }

    /**
     * B-05: Permanently deletes reservation record. Admin only (TC-B-08).
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null || !loggedUser.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }

        String idStr = request.getParameter("id");
        try {
            int id = Integer.parseInt(idStr != null ? idStr.trim() : "");
            boolean deleted = reservationDAO.delete(id);
            if (deleted) {
                SessionUtil.setFlashSuccess(request, "Reservation record permanently deleted.");
            } else {
                SessionUtil.setFlashError(request, "Failed to delete reservation record.");
            }
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(request, "Invalid reservation ID.");
        }
        response.sendRedirect(request.getContextPath() + "/reservations?action=list");
    }
}
