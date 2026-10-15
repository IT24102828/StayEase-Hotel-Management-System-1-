# StayEase System Architecture & OOP Design Document

## 1. Architectural Overview

StayEase is architected using the standard **Model-View-Controller (MVC)** and **Data Access Object (DAO)** enterprise design patterns on the Jakarta EE platform.

```
                      +-----------------------------+
                      |         Web Browser         |
                      +-----------------------------+
                                     | HTTP Requests (GET/POST)
                                     v
+------------------------------------------------------------------------+
|                               CONTROLLERS                              |
|  RoomServlet      UserServlet      ReservationServlet     HomeServlet  |
+------------------------------------------------------------------------+
             |                                        |
             | Forwards View State                    | Interacts with Data Layer
             v                                        v
+-----------------------------+        +---------------------------------+
|            VIEWS            |        |           DATA ACCESS           |
| JSP Pages (WEB-INF/views/)  |        |    RoomDAO           UserDAO    |
| - Header / Footer / Alerts  |        |          ReservationDAO         |
| - JSTL Tag Library          |        +---------------------------------+
| - Bootstrap 5 Styling       |                       |
+-----------------------------+                       | Uses JDBC
                                                      v
                                       +---------------------------------+
                                       |          DOMAIN MODELS          |
                                       |   Room (Single, Double, Suite)  |
                                       |   User (Admin, Regular, Premium)|
                                       |   Reservation                   |
                                       +---------------------------------+
                                                      |
                                                      v
                                       +---------------------------------+
                                       |            DATABASE             |
                                       |        MySQL 8.0 (InnoDB)       |
                                       +---------------------------------+
```

---

## 2. Object-Oriented Programming (OOP) Implementation

Academic compliance with SE1020 requires clear demonstration of the four fundamental OOP principles in Java.

### A. Encapsulation
* **Private State:** All fields in `Room`, `User`, and `Reservation` are declared `private`.
* **Controlled Accessors and Mutators:** Standard getters and setters control access to private state.
* **Domain Validation at the Boundary:**
  - `Room.setRoomNumber(String)` restricts room identifiers to a maximum of 10 characters.
  - `Room.setBasePrice(BigDecimal)` asserts that prices are strictly greater than zero.
  - `User.setFullName(String)` enforces a maximum length of 100 characters.
  - `User.setEmail(String)` normalizes email strings to lowercase.
  - `Reservation.setGuests(int)` enforces valid party sizes between 1 and 10 guests.

### B. Abstraction
* **Abstract Base Classes:**
  - `public abstract class Room`: Defines common hotel room attributes and declares abstract behavior (`getNightlyRate()`, `getCapacity()`, `getFeatures()`) without enforcing concrete calculations.
  - `public abstract class User`: Defines common identity and authentication attributes and declares abstract role behavior (`getDiscountRate()`, `canManageRooms()`).
* **Factory Methods:**
  - `Room.create(String type)` encapsulates subclass instantiation, insulating client code (like servlets and DAOs) from direct `new SingleRoom()` or `new SuiteRoom()` constructor calls.
  - `User.create(String role)` encapsulates specialized user subclass construction.

### C. Inheritance
* **Room Hierarchy:**
  - `Room` (Base Class)
    - `SingleRoom extends Room`
    - `DoubleRoom extends Room`
    - `SuiteRoom extends Room`
* **User Hierarchy:**
  - `User` (Base Class)
    - `AdminUser extends User`
    - `RegularGuest extends User`
    - `PremiumGuest extends User`

Both inheritance trees eliminate code duplication by inheriting common attributes (`id`, `roomNumber`, `basePrice`, `email`, `fullName`) while allowing specialized subclasses to override behavior.

### D. Polymorphism
* **Polymorphic Room Pricing & Capacity:**
  Instead of utilizing complex `if/else` or `switch` statements across the application, pricing rules are bound dynamically to the runtime object type:
  - `SingleRoom.getNightlyRate()` returns `basePrice` (Capacity: 1).
  - `DoubleRoom.getNightlyRate()` returns `basePrice * 1.10` (Capacity: 2).
  - `SuiteRoom.getNightlyRate()` returns `(basePrice * 1.25) + 20.00` (Capacity: 4).
* **Polymorphic Membership Discounts:**
  - `AdminUser.getDiscountRate()` returns `0.00`.
  - `RegularGuest.getDiscountRate()` returns `0.00`.
  - `PremiumGuest.getDiscountRate()` returns `0.10` (10% discount).
* **Dynamic Calculation:**
  `Reservation.calculateTotalPrice(Room room, User user, LocalDate checkIn, LocalDate checkOut)` executes polymorphically:
  ```java
  BigDecimal nightlyRate = room.getNightlyRate(); // Calls SingleRoom, DoubleRoom, or SuiteRoom polymorphically
  BigDecimal gross = nightlyRate.multiply(BigDecimal.valueOf(nights));
  BigDecimal discountMultiplier = BigDecimal.ONE.subtract(user.getDiscountRate()); // Polymorphic discount rate
  return gross.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
  ```

---

## 3. Concurrency & Transaction Management

Preventing double bookings under concurrent web requests is a critical requirement (`TC-B-03`).

### The Double-Booking Race Condition
When two users concurrently submit a reservation for the same room during overlapping dates:
1. User A checks room availability &rarr; Free.
2. User B checks room availability &rarr; Free.
3. User A inserts reservation &rarr; Confirmed.
4. User B inserts reservation &rarr; Confirmed (Overbooked!).

### StayEase Solution: Two-Phase Row-Locking Transaction
Inside `ReservationDAO.insert(Reservation res)`:
1. `conn.setAutoCommit(false)` starts a database transaction.
2. **Pessimistic Lock on Room:**
   ```sql
   SELECT status FROM rooms WHERE id = ? FOR UPDATE;
   ```
   Locks the room row in MySQL/InnoDB. Any concurrent transaction attempting to reserve the same room is blocked at this step until the first transaction completes.
3. **Overlapping Reservation Check:**
   ```sql
   SELECT id FROM reservations 
   WHERE room_id = ? AND status = 'CONFIRMED' 
     AND check_in < ? AND check_out > ?;
   ```
   If a conflicting booking exists, transaction immediately rolls back and throws `DoubleBookingException`.
4. **Insert Reservation:**
   ```sql
   INSERT INTO reservations (...) VALUES (...);
   ```
5. **Commit Transaction:**
   `conn.commit()` releases the lock and confirms the reservation atomically.

---

## 4. Security Architecture

1. **Password Security:**
   - Plaintext passwords are never persisted.
   - Salted hashing via **BCrypt** (`log_rounds = 12`) in `PasswordUtil.java`.
2. **SQL Injection Defense:**
   - 100% of SQL queries across `RoomDAO`, `UserDAO`, and `ReservationDAO` use parameterized `PreparedStatement`.
3. **Cross-Site Scripting (XSS) Prevention:**
   - All JSP views use `<c:out value="..."/>` to automatically escape HTML entities before rendering.
4. **Server-Side Access Control (RBAC):**
   - Protected actions (such as adding rooms or deleting guests) verify `SessionUtil.isAdmin(request)` on the server.
   - Guests can only view and cancel their own reservations (`res.getUserId() == loggedUser.getId()`).
   - Administrators cannot delete their own accounts.
5. **Post/Redirect/Get (PRG) Pattern:**
   - All state-changing POST operations redirect to GET endpoints with flash messages stored temporarily in session, preventing duplicate form resubmissions upon page reload.
