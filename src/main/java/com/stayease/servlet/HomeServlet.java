package com.stayease.servlet;

import com.stayease.dao.RoomDAO;
import com.stayease.model.Room;
import com.stayease.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller Servlet for the Hotel Home / Public Landing page.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private RoomDAO roomDAO;

    @Override
    public void init() {
        this.roomDAO = new RoomDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtil.transferFlashMessages(request);

        // Fetch sample available rooms for showcase on landing page
        List<Room> allRooms = roomDAO.findAll();
        request.setAttribute("featuredRooms", allRooms.stream().filter(Room::isAvailable).limit(3).toList());

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
