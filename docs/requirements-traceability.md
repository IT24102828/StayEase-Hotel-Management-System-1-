# StayEase Requirements Traceability Matrix (RTM)

This document establishes 100% bidirectional traceability between user stories, acceptance criteria, OOP domain models, database DAOs, servlet controllers, JSP views, and automated test cases.

---

## 1. Epic 1: Room Management (Member 1)

| Story ID | Requirement Description | Priority | CRUD | Java Model & OOP | DAO Method | Servlet & Action | JSP View | Test Case | Status |
|---|---|---|---|---|---|---|---|---|---|
| **R-01** | Admin adds a room with validation and duplicate prevention | Must | Create | `Room.create()`, `Room.java` | `RoomDAO.insert()` | `RoomServlet` `action=insert` | `room-add.jsp` | `TC-R-01`, `TC-R-02`, `TC-R-03` | **Verified** |
| **R-02** | Visitors & guests view and search rooms | Must | Read | `Room.java`, Polymorphic methods | `RoomDAO.search()`, `RoomDAO.findAll()` | `RoomServlet` `action=list` | `room-list.jsp` | `TC-R-05`, `TC-R-06` | **Verified** |
| **R-03** | Admin edits room details and pricing | Must | Update | `Room.java`, Encapsulation | `RoomDAO.update()`, `RoomDAO.findById()` | `RoomServlet` `action=update` | `room-edit.jsp` | `TC-R-07` | **Verified** |
| **R-04** | Admin deletes a room with confirmation | Must | Delete | `Room.java` | `RoomDAO.delete()` | `RoomServlet` `action=delete` | `room-list.jsp` | `TC-R-08` | **Verified** |
| **R-05** | Room type polymorphic pricing (Single 1.0x, Double 1.10x, Suite 1.25x+$20) | Must | Read | `SingleRoom`, `DoubleRoom`, `SuiteRoom` | `RoomDAO.findAll()` | `RoomServlet` `action=list` | `room-list.jsp` | `TC-R-09` | **Verified** |
| **R-06** | Maintenance status blocks room from booking | Should | Update | `Room.isMaintenance()`, `Room.isAvailable()` | `RoomDAO.findAvailable()` | `RoomServlet`, `ReservationServlet` | `room-list.jsp`, `room-edit.jsp` | `TC-R-01`, `TC-B-03` | **Verified** |

---

## 2. Epic 2: Guest and Reservation Management (Member 2)

| Story ID | Requirement Description | Priority | CRUD | Java Model & OOP | DAO Method | Servlet & Action | JSP View | Test Case | Status |
|---|---|---|---|---|---|---|---|---|---|
| **U-01** | Guest registration with email uniqueness and BCrypt hashing | Must | Create | `User.create()`, `RegularGuest`, `PremiumGuest` | `UserDAO.insert()` | `UserServlet` `action=register` | `register.jsp` | `TC-U-01`, `TC-U-02`, `TC-U-03` | **Verified** |
| **U-02** | Secure session-based login and logout | Must | Read | `User.java`, `SessionUtil` | `UserDAO.findByEmail()` | `UserServlet` `action=login`, `action=logout` | `login.jsp` | `TC-U-04` | **Verified** |
| **U-03** | Profile update (name, phone, membership, optional password) | Must | Update | `User.java`, Encapsulation | `UserDAO.update()` | `UserServlet` `action=update` | `profile.jsp` | `TC-U-05` | **Verified** |
| **U-04** | Admin searches guest directory by name, email, or ID | Should | Read | `User.java` | `UserDAO.search()` | `UserServlet` `action=list` | `user-list.jsp` | `TC-U-06` | **Verified** |
| **U-05** | Guest deletes own account; Admin deletes other guest accounts | Must | Delete | `User.java`, RBAC | `UserDAO.delete()` | `UserServlet` `action=delete` | `profile.jsp`, `user-list.jsp` | `TC-U-07` | **Verified** |
| **B-01** | Availability search by dates (1–30 nights) and guests (1–10) | Must | Read | `Room.getCapacity()`, `RoomDAO` | `RoomDAO.findAvailable()` | `ReservationServlet` `action=search` | `reservation-form.jsp` | `TC-B-01`, `TC-B-02`, `TC-B-04` | **Verified** |
| **B-02** | Atomic room booking with row lock (`FOR UPDATE`) | Must | Create | `Reservation.java`, `ReservationDAO` | `ReservationDAO.insert()` | `ReservationServlet` `action=book` | `reservation-form.jsp` | `TC-B-03` | **Verified** |
| **B-03** | View reservations (guest sees own; admin sees all) | Must | Read | `Reservation.java`, Privacy isolation | `ReservationDAO.findByUser()`, `findAll()` | `ReservationServlet` `action=list` | `reservation-list.jsp` | `TC-B-07` | **Verified** |
| **B-04** | Cancel active reservation (dates restored to available) | Must | Update | `Reservation.isConfirmed()` | `ReservationDAO.cancel()` | `ReservationServlet` `action=cancel` | `reservation-list.jsp` | `TC-B-06` | **Verified** |
| **B-05** | Admin permanently deletes reservation record | Should | Delete | `Reservation.java` | `ReservationDAO.delete()` | `ReservationServlet` `action=delete` | `reservation-list.jsp` | `TC-B-08` | **Verified** |
| **B-06** | Premium VIP polymorphic discount (10% off total price) | Must | Read | `PremiumGuest.getDiscountRate()` | `Reservation.calculateTotalPrice()` | `ReservationServlet` `action=book` | `reservation-form.jsp` | `TC-B-05` | **Verified** |

---

## 3. Non-Functional Requirements Traceability

| ID | Non-Functional Requirement | Implementation & Architectural Evidence | Status |
|---|---|---|---|
| **NF-01** | **Security:** Parameterized SQL, BCrypt password hashing, HTML escaping, server-side RBAC | PreparedStatements in all DAOs; `PasswordUtil` with BCrypt log rounds 12; `<c:out>` in all JSPs; `SessionUtil.isAdmin(request)` in servlets | **Satisfied** |
| **NF-02** | **Usability:** Responsive layout, consistent labels, flash alerts, modal dialogs | Bootstrap 5.3 responsive grids; `style.css` custom tokens; `alerts.jsp` for PRG flash messages; Bootstrap modal dialogs | **Satisfied** |
| **NF-03** | **Data Integrity:** Foreign keys, cascades, date bounds, atomic row-locking transactions | MySQL InnoDB foreign keys (`ON DELETE CASCADE`); `ReservationDAO` transactional locking (`SELECT ... FOR UPDATE`); check constraints | **Satisfied** |
| **NF-04** | **Performance:** Sub-2 second response times | Composite indexes on `(room_id, check_in, check_out, status)` and `(type, status, base_price)` | **Satisfied** |
| **NF-05** | **Maintainability:** Clear layer separation (Model &rarr; DAO &rarr; Servlet &rarr; JSP) | Pure MVC architecture; no SQL in JSPs; no HTML inside Servlets; single-responsibility packages | **Satisfied** |
| **NF-06** | **Portability:** Cross-platform on JDK 17+ and MySQL 8 | Standard Maven build; portable SQL schema; externalized configuration via `db.properties` | **Satisfied** |
