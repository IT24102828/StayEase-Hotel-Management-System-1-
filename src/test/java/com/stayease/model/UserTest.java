package com.stayease.model;

import com.stayease.TestDatabaseHelper;
import com.stayease.dao.UserDAO;
import com.stayease.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite for Epic 2: User Management.
 * Verifies OOP User inheritance, polymorphic discount calculations, and UserDAO CRUD.
 * Covers requirements U-01 to U-05 and test cases TC-U-01 through TC-U-07.
 */
public class UserTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.initializeTestDatabase();
        userDAO = new UserDAO();
    }

    @Test
    @DisplayName("Polymorphic discount and permissions by User subclass")
    public void testUserPolymorphism() {
        User admin = User.create("ADMIN");
        assertTrue(admin.canManageRooms());
        assertEquals(BigDecimal.ZERO, admin.getDiscountRate());

        User regular = User.create("REGULAR");
        assertFalse(regular.canManageRooms());
        assertEquals(BigDecimal.ZERO, regular.getDiscountRate());

        User premium = User.create("PREMIUM");
        assertFalse(premium.canManageRooms());
        assertEquals(new BigDecimal("0.10"), premium.getDiscountRate());
    }

    @Test
    @DisplayName("TC-U-01: Register with a new email successfully")
    public void testRegisterNewUser() throws Exception {
        String hash = PasswordUtil.hashPassword("Password@123");
        User newUser = User.create(0, "Kasun Silva", "kasun@example.com", "+94770001122", hash, "REGULAR");
        int generatedId = userDAO.insert(newUser);
        assertTrue(generatedId > 0);

        User retrieved = userDAO.findByEmail("kasun@example.com");
        assertNotNull(retrieved);
        assertEquals("Kasun Silva", retrieved.getFullName());
        assertTrue(PasswordUtil.checkPassword("Password@123", retrieved.getPasswordHash()));
    }

    @Test
    @DisplayName("TC-U-02: Register with an email already used (Duplicate Rejection)")
    public void testRegisterDuplicateEmail() {
        String hash = PasswordUtil.hashPassword("Password@123");
        User duplicate = User.create(0, "Duplicate User", "nimal@gmail.com", "+94770009999", hash, "REGULAR");
        assertThrows(SQLException.class, () -> {
            userDAO.insert(duplicate);
        });
    }

    @Test
    @DisplayName("TC-U-03: Password validation length rejection")
    public void testPasswordValidation() {
        // Enforced in PasswordUtil / Servlet validation
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword("");
        });
    }

    @Test
    @DisplayName("TC-U-04: Log in with wrong password fails verification")
    public void testLoginWrongPassword() {
        User user = userDAO.findByEmail("nimal@gmail.com");
        assertNotNull(user);
        assertFalse(PasswordUtil.checkPassword("WrongPassword123", user.getPasswordHash()));
        assertTrue(PasswordUtil.checkPassword("Password@123", user.getPasswordHash()));
    }

    @Test
    @DisplayName("TC-U-05: Change phone and leave password blank (preserves existing password)")
    public void testUpdatePhonePreservingPassword() throws Exception {
        User user = userDAO.findByEmail("nimal@gmail.com");
        assertNotNull(user);
        String originalHash = user.getPasswordHash();

        user.setPhone("+94711112233");
        // Update without changing password
        boolean updated = userDAO.update(user, false);
        assertTrue(updated);

        User reloaded = userDAO.findByEmail("nimal@gmail.com");
        assertEquals("+94711112233", reloaded.getPhone());
        assertEquals(originalHash, reloaded.getPasswordHash());
        assertTrue(PasswordUtil.checkPassword("Password@123", reloaded.getPasswordHash()));
    }

    @Test
    @DisplayName("TC-U-06: Admin searches 'nimal' returns matching guest")
    public void testSearchUser() {
        List<User> list = userDAO.search("nimal");
        assertFalse(list.isEmpty());
        boolean found = false;
        for (User u : list) {
            if (u.getEmail().equalsIgnoreCase("nimal@gmail.com")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Should find guest with 'nimal'");
    }

    @Test
    @DisplayName("TC-U-07: Guest deletes own account (record removed)")
    public void testDeleteAccount() {
        User user = userDAO.findByEmail("nimal@gmail.com");
        assertNotNull(user);

        boolean deleted = userDAO.delete(user.getId());
        assertTrue(deleted);

        User checkAfter = userDAO.findById(user.getId());
        assertNull(checkAfter);
    }
}
