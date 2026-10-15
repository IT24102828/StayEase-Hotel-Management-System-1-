package com.stayease.servlet;

import com.stayease.dao.RoomDAO;
import com.stayease.model.Room;
import com.stayease.model.User;
import com.stayease.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller Servlet managing hotel room inventory.
 * Supports browsing, searching, creating, editing, and deleting rooms.
 *
 * Owned by Member 1 (Epic 1: Room Management).
 */
@WebServlet(name = "RoomServlet", urlPatterns = {"/rooms"})
public class RoomServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RoomServlet.class.getName());
    private RoomDAO roomDAO;

    @Override
    public void init() {
        this.roomDAO = new RoomDAO();
    }

    // For testing injection
    public void setRoomDAO(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtil.transferFlashMessages(request);
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new" -> showAddForm(request, response);
            case "edit" -> showEditForm(request, response);
            case "list" -> listRooms(request, response);
            default -> listRooms(request, response);
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
            case "insert" -> insertRoom(request, response);
            case "update" -> updateRoom(request, response);
            case "delete" -> deleteRoom(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    /**
     * R-02: Lists and searches rooms. Accessible by visitors, guests, and admins.
     */
    private void listRooms(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Room> rooms;

        if (keyword != null && !keyword.trim().isEmpty()) {
            rooms = roomDAO.search(keyword.trim());
            request.setAttribute("keyword", keyword.trim());
        } else {
            rooms = roomDAO.findAll();
        }

        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/WEB-INF/views/room/room-list.jsp").forward(request, response);
    }

    /**
     * Displays the Add Room form. Admin only.
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/room/room-add.jsp").forward(request, response);
    }

    /**
     * Displays the Edit Room form. Admin only.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            SessionUtil.setFlashError(request, "Room ID is required to edit.");
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            Room room = roomDAO.findById(id);
            if (room == null) {
                SessionUtil.setFlashError(request, "Room not found.");
                response.sendRedirect(request.getContextPath() + "/rooms");
                return;
            }

            request.setAttribute("room", room);
            request.getRequestDispatcher("/WEB-INF/views/room/room-edit.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(request, "Invalid room ID.");
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    /**
     * R-01: Creates a new room. Admin only.
     */
    private void insertRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }

        String roomNumber = request.getParameter("roomNumber");
        String type = request.getParameter("type");
        String basePriceStr = request.getParameter("basePrice");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        // Validate required fields
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Room number is required.");
            forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
            return;
        }
        roomNumber = roomNumber.trim();
        if (roomNumber.length() > 10) {
            request.setAttribute("errorMessage", "Room number cannot exceed 10 characters.");
            forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
            return;
        }

        if (type == null || (!type.equalsIgnoreCase("Single") && !type.equalsIgnoreCase("Double") && !type.equalsIgnoreCase("Suite"))) {
            request.setAttribute("errorMessage", "Please select a valid room type (Single, Double, or Suite).");
            forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
            return;
        }

        BigDecimal basePrice;
        try {
            basePrice = new BigDecimal(basePriceStr != null ? basePriceStr.trim() : "");
            if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("errorMessage", "Base price must be greater than zero.");
                forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Please enter a valid positive base price.");
            forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
            return;
        }

        if (status == null || (!status.equalsIgnoreCase("Available") && !status.equalsIgnoreCase("Maintenance"))) {
            status = "Available";
        }

        // Check for duplicate room number (TC-R-02)
        Room existing = roomDAO.findByRoomNumber(roomNumber);
        if (existing != null) {
            request.setAttribute("errorMessage", "Room number " + roomNumber + " already exists.");
            forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
            return;
        }

        try {
            Room newRoom = Room.create(0, roomNumber, type, basePrice, status, description);
            roomDAO.insert(newRoom);
            SessionUtil.setFlashSuccess(request, "Room added.");
            response.sendRedirect(request.getContextPath() + "/rooms");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error adding room", e);
            request.setAttribute("errorMessage", "Failed to add room due to database error.");
            forwardAddWithValues(request, response, roomNumber, type, basePriceStr, status, description);
        }
    }

    private void forwardAddWithValues(HttpServletRequest request, HttpServletResponse response,
                                      String roomNumber, String type, String basePrice, String status, String description)
            throws ServletException, IOException {
        request.setAttribute("inputRoomNumber", roomNumber);
        request.setAttribute("inputType", type);
        request.setAttribute("inputBasePrice", basePrice);
        request.setAttribute("inputStatus", status);
        request.setAttribute("inputDescription", description);
        request.getRequestDispatcher("/WEB-INF/views/room/room-add.jsp").forward(request, response);
    }

    /**
     * R-03: Updates an existing room. Admin only.
     */
    private void updateRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }

        String idStr = request.getParameter("id");
        String roomNumber = request.getParameter("roomNumber");
        String type = request.getParameter("type");
        String basePriceStr = request.getParameter("basePrice");
        String status = request.getParameter("status");
        String description = request.getParameter("description");

        int id;
        try {
            id = Integer.parseInt(idStr != null ? idStr.trim() : "");
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(request, "Invalid room ID.");
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        Room currentRoom = roomDAO.findById(id);
        if (currentRoom == null) {
            SessionUtil.setFlashError(request, "Room not found.");
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        if (roomNumber == null || roomNumber.trim().isEmpty() || roomNumber.trim().length() > 10) {
            request.setAttribute("errorMessage", "Room number is required and cannot exceed 10 characters.");
            request.setAttribute("room", currentRoom);
            request.getRequestDispatcher("/WEB-INF/views/room/room-edit.jsp").forward(request, response);
            return;
        }
        roomNumber = roomNumber.trim();

        // Check if room number was changed and if new room number is already taken
        Room existingWithNumber = roomDAO.findByRoomNumber(roomNumber);
        if (existingWithNumber != null && existingWithNumber.getId() != id) {
            request.setAttribute("errorMessage", "Room number " + roomNumber + " already exists.");
            request.setAttribute("room", currentRoom);
            request.getRequestDispatcher("/WEB-INF/views/room/room-edit.jsp").forward(request, response);
            return;
        }

        BigDecimal basePrice;
        try {
            basePrice = new BigDecimal(basePriceStr != null ? basePriceStr.trim() : "");
            if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("errorMessage", "Base price must be greater than zero.");
                request.setAttribute("room", currentRoom);
                request.getRequestDispatcher("/WEB-INF/views/room/room-edit.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Please enter a valid positive base price.");
            request.setAttribute("room", currentRoom);
            request.getRequestDispatcher("/WEB-INF/views/room/room-edit.jsp").forward(request, response);
            return;
        }

        try {
            Room updatedRoom = Room.create(id, roomNumber, type, basePrice, status, description);
            roomDAO.update(updatedRoom);
            SessionUtil.setFlashSuccess(request, "Room updated.");
            response.sendRedirect(request.getContextPath() + "/rooms");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error updating room", e);
            request.setAttribute("errorMessage", "Failed to update room due to database error.");
            request.setAttribute("room", currentRoom);
            request.getRequestDispatcher("/WEB-INF/views/room/room-edit.jsp").forward(request, response);
        }
    }

    /**
     * R-04: Deletes a room. Admin only.
     */
    private void deleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }

        String idStr = request.getParameter("id");
        try {
            int id = Integer.parseInt(idStr != null ? idStr.trim() : "");
            boolean deleted = roomDAO.delete(id);
            if (deleted) {
                SessionUtil.setFlashSuccess(request, "Room deleted.");
            } else {
                SessionUtil.setFlashError(request, "Failed to delete room.");
            }
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(request, "Invalid room ID.");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }
}
