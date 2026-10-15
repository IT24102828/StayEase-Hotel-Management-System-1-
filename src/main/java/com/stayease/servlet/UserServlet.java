package com.stayease.servlet;

import com.stayease.dao.UserDAO;
import com.stayease.model.User;
import com.stayease.util.PasswordUtil;
import com.stayease.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Controller Servlet for User Management, Authentication, and Profiles.
 *
 * Owned by Member 2 (Epic 2: Guest and Reservation Management).
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UserServlet.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-]{7,20}$");

    private UserDAO userDAO;

    @Override
    public void init() {
        this.userDAO = new UserDAO();
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionUtil.transferFlashMessages(request);
        String action = request.getParameter("action");
        if (action == null) {
            action = "login";
        }

        switch (action) {
            case "register" -> showRegisterForm(request, response);
            case "login" -> showLoginForm(request, response);
            case "logout" -> handleLogout(request, response);
            case "profile" -> showProfile(request, response);
            case "list" -> listUsers(request, response);
            default -> showLoginForm(request, response);
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
            case "register" -> handleRegister(request, response);
            case "login" -> handleLogin(request, response);
            case "update" -> handleUpdateProfile(request, response);
            case "delete" -> handleDeleteUser(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/user?action=login");
        }
    }

    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(request, response);
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            User user = SessionUtil.getLoggedInUser(request);
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/rooms");
            } else {
                response.sendRedirect(request.getContextPath() + "/reservations?action=search");
            }
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SessionUtil.logout(request);
        SessionUtil.setFlashSuccess(request, "Logged out successfully.");
        response.sendRedirect(request.getContextPath() + "/user?action=login");
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        User freshUser = userDAO.findById(loggedUser.getId());
        if (freshUser == null) {
            SessionUtil.logout(request);
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        request.setAttribute("user", freshUser);
        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
    }

    /**
     * U-04: Admin searches guests.
     */
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator rights required.");
            return;
        }

        String keyword = request.getParameter("keyword");
        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userDAO.search(keyword.trim());
            request.setAttribute("keyword", keyword.trim());
        } else {
            users = userDAO.findAll();
        }

        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/user/user-list.jsp").forward(request, response);
    }

    /**
     * U-01: Guest Registration.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String membership = request.getParameter("membership");

        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Full name is required.");
            forwardRegister(request, response, fullName, email, phone, membership);
            return;
        }
        fullName = fullName.trim();
        if (fullName.length() > 100) {
            request.setAttribute("errorMessage", "Full name cannot exceed 100 characters.");
            forwardRegister(request, response, fullName, email, phone, membership);
            return;
        }

        if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            request.setAttribute("errorMessage", "Please provide a valid email address.");
            forwardRegister(request, response, fullName, email, phone, membership);
            return;
        }
        email = email.trim().toLowerCase();

        if (phone != null && !phone.trim().isEmpty()) {
            phone = phone.trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                request.setAttribute("errorMessage", "Phone number must be between 7 and 20 digits (optional +, - or spaces).");
                forwardRegister(request, response, fullName, email, phone, membership);
                return;
            }
        } else {
            phone = null;
        }

        if (password == null || password.length() < 6) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters long.");
            forwardRegister(request, response, fullName, email, phone, membership);
            return;
        }

        if (membership == null || (!membership.equalsIgnoreCase("REGULAR") && !membership.equalsIgnoreCase("PREMIUM"))) {
            membership = "REGULAR";
        }
        membership = membership.toUpperCase();

        // Duplicate email check (TC-U-02)
        User existingUser = userDAO.findByEmail(email);
        if (existingUser != null) {
            request.setAttribute("errorMessage", "That email is already registered.");
            forwardRegister(request, response, fullName, email, phone, membership);
            return;
        }

        try {
            String passwordHash = PasswordUtil.hashPassword(password);
            User newUser = User.create(0, fullName, email, phone, passwordHash, membership);
            userDAO.insert(newUser);

            SessionUtil.setFlashSuccess(request, "Account created.");
            response.sendRedirect(request.getContextPath() + "/user?action=login");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during registration", e);
            request.setAttribute("errorMessage", "An error occurred during account creation. Please try again.");
            forwardRegister(request, response, fullName, email, phone, membership);
        }
    }

    private void forwardRegister(HttpServletRequest request, HttpServletResponse response,
                                 String fullName, String email, String phone, String membership)
            throws ServletException, IOException {
        request.setAttribute("inputFullName", fullName);
        request.setAttribute("inputEmail", email);
        request.setAttribute("inputPhone", phone);
        request.setAttribute("inputMembership", membership);
        request.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(request, response);
    }

    /**
     * U-02: User Authentication.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Email or password is incorrect.");
            request.setAttribute("inputEmail", email);
            request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null || !PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            // Generic security error message (TC-U-04)
            request.setAttribute("errorMessage", "Email or password is incorrect.");
            request.setAttribute("inputEmail", email);
            request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
            return;
        }

        // Authentication successful
        SessionUtil.setLoggedInUser(request, user);
        SessionUtil.setFlashSuccess(request, "Welcome back, " + user.getFullName() + "!");

        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/rooms");
        } else {
            response.sendRedirect(request.getContextPath() + "/reservations?action=search");
        }
    }

    /**
     * U-03: Profile update.
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        User user = userDAO.findById(loggedUser.getId());
        if (user == null) {
            SessionUtil.logout(request);
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String newPassword = request.getParameter("password");
        String membership = request.getParameter("membership");

        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Full name cannot be empty.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
            return;
        }
        fullName = fullName.trim();
        if (fullName.length() > 100) {
            request.setAttribute("errorMessage", "Full name cannot exceed 100 characters.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
            return;
        }

        if (phone != null && !phone.trim().isEmpty()) {
            phone = phone.trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                request.setAttribute("errorMessage", "Phone number must be between 7 and 20 digits (optional +, - or spaces).");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
                return;
            }
        } else {
            phone = null;
        }

        boolean updatePassword = false;
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 6) {
                request.setAttribute("errorMessage", "New password must be at least 6 characters long.");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
                return;
            }
            user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            updatePassword = true;
        }

        // Handle role/membership: Admin cannot change their own role
        if (!user.isAdmin() && membership != null) {
            if (membership.equalsIgnoreCase("PREMIUM") || membership.equalsIgnoreCase("REGULAR")) {
                user.setRole(membership.toUpperCase());
            }
        }

        user.setFullName(fullName);
        user.setPhone(phone);

        try {
            userDAO.update(user, updatePassword);
            // Refresh session user
            User refreshed = userDAO.findById(user.getId());
            SessionUtil.setLoggedInUser(request, refreshed);
            SessionUtil.setFlashSuccess(request, "Profile updated successfully.");
            response.sendRedirect(request.getContextPath() + "/user?action=profile");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile", e);
            request.setAttribute("errorMessage", "Failed to update profile due to database error.");
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
        }
    }

    /**
     * U-05: Account deletion.
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User loggedUser = SessionUtil.getLoggedInUser(request);
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/user?action=login");
            return;
        }

        String targetIdStr = request.getParameter("id");
        int targetId;
        try {
            targetId = Integer.parseInt(targetIdStr != null ? targetIdStr.trim() : "");
        } catch (NumberFormatException e) {
            SessionUtil.setFlashError(request, "Invalid account ID.");
            response.sendRedirect(request.getContextPath() + (loggedUser.isAdmin() ? "/user?action=list" : "/user?action=profile"));
            return;
        }

        // Rule: Admins cannot delete their own account (U-05)
        if (loggedUser.isAdmin() && targetId == loggedUser.getId()) {
            SessionUtil.setFlashError(request, "Administrators cannot delete their own account.");
            response.sendRedirect(request.getContextPath() + "/user?action=list");
            return;
        }

        // Rule: Guests can only delete their own account (TC-U-07)
        if (!loggedUser.isAdmin() && targetId != loggedUser.getId()) {
            SessionUtil.setFlashError(request, "You are only permitted to delete your own account.");
            response.sendRedirect(request.getContextPath() + "/user?action=profile");
            return;
        }

        boolean deleted = userDAO.delete(targetId);
        if (deleted) {
            if (!loggedUser.isAdmin() || targetId == loggedUser.getId()) {
                // Guest self-delete: log out and redirect to login
                SessionUtil.logout(request);
                SessionUtil.setFlashSuccess(request, "Account deleted.");
                response.sendRedirect(request.getContextPath() + "/user?action=login");
            } else {
                // Admin deleted another guest account
                SessionUtil.setFlashSuccess(request, "Guest account deleted.");
                response.sendRedirect(request.getContextPath() + "/user?action=list");
            }
        } else {
            SessionUtil.setFlashError(request, "Failed to delete account.");
            response.sendRedirect(request.getContextPath() + (loggedUser.isAdmin() ? "/user?action=list" : "/user?action=profile"));
        }
    }
}
