# StayEase SE1020 Viva Presentation & Defense Guide

This comprehensive viva guide prepares both undergraduate team members for their oral defense and technical demonstration in **SE1020 Object Oriented Programming**.

---

## 1. Viva Structure & General Tips

During the viva, the examiner will focus primarily on:
1. **Live Demonstration:** Demonstrating features using the running web application on Tomcat 10.1 or IntelliJ IDEA.
2. **Individual Contributions:** Verifying that each student wrote, understands, and can explain their assigned epic.
3. **Backend Java Code:** Inspecting Java classes, data access methods, and servlets.
4. **OOP Principles:** Pointing out encapsulation, abstraction, inheritance, and polymorphism in source code.
5. **Database & Concurrency:** Explaining database tables, JDBC `PreparedStatement`, transactions, and double-booking prevention.
6. **Git History:** Demonstrating regular collaborative commits from both students' GitHub accounts.

---

## 2. Member 1 Defense Guide (Epic 1: Room Management)

### A. Assigned Modules & Ownership
* **Java Models:** `Room.java` (abstract base), `SingleRoom.java`, `DoubleRoom.java`, `SuiteRoom.java`
* **DAO Layer:** `RoomDAO.java`
* **Controller Layer:** `RoomServlet.java`
* **JSP Views:** `room-list.jsp`, `room-add.jsp`, `room-edit.jsp`
* **Test Suite:** `RoomTest.java`

### B. What to Demonstrate in the Browser
1. **Browse & Search Rooms:** Open `/rooms`. Search for `"Suite"` &rarr; show that only Suite rooms appear. Search for `"zzz"` &rarr; show the empty state message: *"No rooms match your search."*
2. **Add New Room (Admin):** Log in as Admin (`admin@stayease.com` / `Password@123`). Navigate to `/rooms?action=new`. Add Room `401`, Double, base price `$95.00`, Available. Show that the calculated rate is `$104.50` ($95 &times; 1.10).
3. **Duplicate Prevention:** Try adding Room `101` again &rarr; show error message: *"Room number 101 already exists."*
4. **Edit Room:** Click Edit on Room `102`. Change base price to `$70.00`. Save and verify updated nightly rate.
5. **Delete Room:** Click Delete on Room `302`. Confirm in modal dialog &rarr; show success message: *"Room deleted."*
6. **Maintenance Mode:** Show Room `203` (Maintenance status badge). Explain that maintenance rooms are visible in the room directory with a grey badge but automatically excluded from guest booking searches.

### C. Explaining OOP Concepts in Member 1's Code
* **Encapsulation:** Open `Room.java`. Show private fields (`roomNumber`, `basePrice`, `status`). Show `setBasePrice(BigDecimal)` validating that price is strictly positive, and `setRoomNumber(String)` enforcing maximum 10 characters.
* **Abstraction:** Point out `public abstract class Room`. Explain that `getNightlyRate()`, `getCapacity()`, and `getFeatures()` are abstract methods defining a contract without dictating calculation details.
* **Inheritance:** Open `DoubleRoom.java` and `SuiteRoom.java`. Show `extends Room`. Both inherit common attributes (`id`, `roomNumber`, `description`) without duplicating fields.
* **Polymorphism:** Show how `getNightlyRate()` calculates differently per subclass:
  - Single: `basePrice` (1.0x)
  - Double: `basePrice.multiply(new BigDecimal("1.10"))` (+10%)
  - Suite: `basePrice.multiply(new BigDecimal("1.25")).add(new BigDecimal("20.00"))` (+25% plus $20 fee)
* **Factory Pattern:** Open `Room.create(String type)`. Show how the factory method encapsulates subclass instantiation based on type string.

### D. Potential Viva Questions for Member 1
* **Q1: Why did you make `Room` an abstract class instead of a regular class?**  
  *Answer:* A generic "Room" does not exist in the physical hotel &mdash; every physical room has a specific configuration (Single, Double, or Suite). Making `Room` abstract prevents direct instantiation and mandates that concrete subclasses provide specific nightly pricing formulas, capacity, and amenities.
* **Q2: Why use `BigDecimal` instead of `double` for prices?**  
  *Answer:* `double` and `float` use binary floating-point representation, which causes rounding errors in monetary arithmetic (e.g., `0.1 + 0.2 = 0.30000000000000004`). `BigDecimal` provides exact decimal representation and precise rounding modes (`RoundingMode.HALF_UP`).
* **Q3: How does `RoomDAO.search(String keyword)` prevent SQL injection?**  
  *Answer:* It uses a parameterized `PreparedStatement` with `?` placeholders. The user input is passed via `ps.setString()`, which treats the input purely as literal data rather than executable SQL statements.

---

## 3. Member 2 Defense Guide (Epic 2: Guest and Reservation Management)

### A. Assigned Modules & Ownership
* **Java Models:** `User.java` (abstract base), `AdminUser.java`, `RegularGuest.java`, `PremiumGuest.java`, `Reservation.java`
* **DAO Layer:** `UserDAO.java`, `ReservationDAO.java`
* **Controller Layer:** `UserServlet.java`, `ReservationServlet.java`
* **JSP Views:** `register.jsp`, `login.jsp`, `profile.jsp`, `user-list.jsp`, `reservation-form.jsp`, `reservation-list.jsp`
* **Test Suite:** `UserTest.java`, `ReservationTest.java`

### B. What to Demonstrate in the Browser
1. **Guest Registration:** Open `/user?action=register`. Register a new user (`test@example.com`, password `Password@123`, Premium tier). Show that duplicate emails are rejected.
2. **Login & Session Redirection:** Log in as guest &rarr; redirect to `/reservations?action=search`. Log in as admin &rarr; redirect to `/rooms`.
3. **Availability Search & Pricing (TC-B-05):** Search for a stay of 3 nights for Suite `301` as a Premium guest. Show that the total is exactly **$661.50** ($245 &times; 3 nights = $735 &minus; 10% VIP discount).
4. **Transactional Booking & Zero Double Booking (TC-B-03):** Book Room `201` for Nov 10 to Nov 15. In a second incognito browser, search the same dates &rarr; show that Room `201` is now unavailable.
5. **My Bookings & Cancellation (TC-B-06):** View `/reservations?action=list`. Cancel the booking. Return to the availability search &rarr; show that Room `201` is immediately available again.
6. **Guest Isolation (TC-B-07):** Log in as Regular guest Nimal &rarr; show only Nimal's bookings. Log in as Sunil &rarr; show only Sunil's bookings.
7. **Admin Guest Search & Deletion (U-04/U-05):** Log in as Admin. Open `/user?action=list`. Search `"nimal"`. Show that the Admin cannot delete their own account.

### C. Explaining OOP Concepts in Member 2's Code
* **Encapsulation:** Open `User.java` and `Reservation.java`. Show private fields. In `Reservation.setGuests(int)`, show validation enforcing between 1 and 10 guests.
* **Abstraction & Inheritance:** Point out `abstract class User` with subclasses `AdminUser`, `RegularGuest`, `PremiumGuest`.
* **Polymorphism:** Show `getDiscountRate()` returning `BigDecimal.ZERO` for Regular/Admin and `new BigDecimal("0.10")` for Premium.
* **Polymorphic Execution:** In `Reservation.calculateTotalPrice(Room, User, checkIn, checkOut)`, show that the pricing method takes abstract `Room` and abstract `User` references and invokes `room.getNightlyRate()` and `user.getDiscountRate()` polymorphically at runtime.
* **Concurrency Locking:** Open `ReservationDAO.insert(Reservation res)`. Walk through:
  1. `conn.setAutoCommit(false)`
  2. `SELECT status FROM rooms WHERE id = ? FOR UPDATE` (Pessimistic lock)
  3. Overlapping date query check
  4. Safe insert & `conn.commit()`
  5. `catch` block rolling back transaction and `finally` restoring auto-commit.

### D. Potential Viva Questions for Member 2
* **Q1: How do you prevent double bookings when two guests book the same room at the same millisecond?**  
  *Answer:* We use a database transaction with a row-level lock: `SELECT status FROM rooms WHERE id = ? FOR UPDATE`. This locks the room row in the InnoDB engine. The second concurrent transaction is forced to wait until the first commits. When the second transaction acquires the lock, it re-queries for overlapping confirmed reservations, finds the newly committed booking, and is rejected with a `DoubleBookingException`.
* **Q2: Why did you use BCrypt for password hashing?**  
  *Answer:* Plain hashes like SHA-256 or MD5 are vulnerable to rainbow table attacks and fast GPU brute-forcing. BCrypt includes a cryptographic random salt and has a configurable work factor (`log_rounds = 12`), making brute-force dictionary attacks computationally infeasible.
* **Q3: How does your overlap check allow back-to-back reservations?**  
  *Answer:* We define check-out as exclusive: `existing.check_in < requested.check_out AND existing.check_out > requested.check_in`. If Guest A checks out on Nov 5, and Guest B checks in on Nov 5, `existing.check_out (Nov 5) > requested.check_in (Nov 5)` is false, so no overlap is detected and the booking is safely permitted.

---

## 4. Shared Questions & Architecture Defense

* **Q: What architecture did you use for this application?**  
  *Answer:* We used standard Model-View-Controller (MVC) and Data Access Object (DAO). Servlets act as Controllers handling HTTP requests and validation; JSP files act as Views rendering HTML; DAOs handle JDBC persistence; and model classes represent encapsulated OOP entities.
* **Q: How does this project meet the requirements of a 2-person university team?**  
  *Answer:* The project is split into two distinct, balanced epics:
  - Member 1 delivered Epic 1 (Room Management): 4 CRUD operations, 3 UI screens (`room-list.jsp`, `room-add.jsp`, `room-edit.jsp`), and Room OOP hierarchy.
  - Member 2 delivered Epic 2 (Guest & Reservation Management): 8 CRUD operations, 6 UI screens, User OOP hierarchy, and concurrency-safe booking.
* **Q: How did you collaborate on GitHub?**  
  *Answer:* We used a feature-branch workflow with `main`, `feature/epic1-rooms`, and `feature/epic2-guests-reservations`. Shared utilities like `DBConnection` and documentation were merged via reviewed Pull Requests.
