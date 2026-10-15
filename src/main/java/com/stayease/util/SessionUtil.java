package com.stayease.util;

import com.stayease.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Helper utility for session and authentication management.
 */
public final class SessionUtil {

    public static final String SESSION_USER_KEY = "currentUser";
    public static final String FLASH_SUCCESS_KEY = "successMessage";
    public static final String FLASH_ERROR_KEY = "errorMessage";

    private SessionUtil() {
    }

    /**
     * Retrieves currently authenticated user from HTTP session.
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userObj = session.getAttribute(SESSION_USER_KEY);
            if (userObj instanceof User) {
                return (User) userObj;
            }
        }
        return null;
    }

    /**
     * Checks if a user is currently logged in.
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }

    /**
     * Checks if currently logged in user is an Administrator.
     */
    public static boolean isAdmin(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null && user.isAdmin();
    }

    /**
     * Stores user in session upon successful authentication.
     */
    public static void setLoggedInUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER_KEY, user);
    }

    /**
     * Clears user session upon logout.
     */
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Sets flash success message in session for Post/Redirect/Get pattern.
     */
    public static void setFlashSuccess(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH_SUCCESS_KEY, message);
    }

    /**
     * Sets flash error message in session for Post/Redirect/Get pattern.
     */
    public static void setFlashError(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH_ERROR_KEY, message);
    }

    /**
     * Consumes flash messages and sets them as request attributes for JSP rendering.
     */
    public static void transferFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object success = session.getAttribute(FLASH_SUCCESS_KEY);
            if (success != null) {
                request.setAttribute(FLASH_SUCCESS_KEY, success);
                session.removeAttribute(FLASH_SUCCESS_KEY);
            }
            Object error = session.getAttribute(FLASH_ERROR_KEY);
            if (error != null) {
                request.setAttribute(FLASH_ERROR_KEY, error);
                session.removeAttribute(FLASH_ERROR_KEY);
            }
        }
    }
}
