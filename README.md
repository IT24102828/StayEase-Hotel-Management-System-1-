# StayEase Hotel Room Reservation System

> **Course:** SE1020 – Object Oriented Programming (University Project)  
> **Team:** Two-Member Undergraduate Engineering Team  
> **Architecture:** Java 17, Jakarta Servlet 6.0, JSP, JDBC, MySQL 8.0, Bootstrap 5, Maven  
> **Target Container:** Apache Tomcat 10.1  

---

## 1. Project Overview

**StayEase** is a full-stack, enterprise-grade Hotel Room Reservation web application designed for boutique hotel operations and compliant with the academic requirements of the **SE1020 Object Oriented Programming** module.

The system replaces manual paper/telephone bookings with a responsive web portal providing real-time room availability, concurrency-safe transactions with zero double bookings, polymorphic room pricing and membership discounts, comprehensive role-based access control, and complete administrative inventory management.

---

## 2. Key Features

### A. Guest-Facing Experience
* **Browse Rooms & Suites:** Real-time room directory displaying Single, Double, and Executive Suites with capacity, amenities, base rates, and polymorphic nightly formulas.
* **Instant Availability Search:** Search free rooms by check-in date, check-out date, and guest count (1–10 guests).
* **Guaranteed Booking:** Server-side transactional row-locking (`SELECT ... FOR UPDATE`) prevents concurrent double bookings.
* **Polymorphic VIP Discounts:** Premium members receive an automatic 10% discount on all room stays.
* **Guest Account & Profile Portal:** Self-service registration, profile update, membership upgrades, and one-click account deletion.
* **My Bookings & Cancellation:** View personal reservation history and cancel active stays (freeing dates immediately).

### B. Hotel Administration Console
* **Inventory Management:** Full CRUD operations on hotel rooms (Create, Read, Update, Delete).
* **Operational Status Control:** Toggle rooms between `Available` and `Maintenance` (maintenance rooms are automatically hidden from guest bookings).
* **Guest Directory & Administration:** Search registered guests by name, email, or guest ID. Admin can remove guest accounts while protected from self-deletion.
* **Master Reservation Records:** View all reservations across the hotel with guest contact details.
* **Permanent Record Deletion:** Administrators can cancel any reservation or permanently purge records.

---

## 3. Technology Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Backend Runtime** | Java 17 (OpenJDK) | Core language, OOP domain models |
| **Web Tier** | Jakarta Servlet API 6.0, JSP 3.1, JSTL 3.0 | MVC Controller & Server-Side Views |
| **Data Access** | JDBC with `PreparedStatement` | High-efficiency parameterized SQL, transaction management |
| **Database** | MySQL 8.0 (InnoDB Engine) | Relational persistence, foreign key cascades, row-level locks |
| **Security** | BCrypt (`jbcrypt:0.4`) | Salted password hashing (no plaintext storage) |
| **Application Server** | Apache Tomcat 10.1 | Jakarta EE 10 Servlet Container |
| **Frontend UI/UX** | HTML5, CSS3, Bootstrap 5.3, Bootstrap Icons | Luxury hospitality aesthetic, responsive layouts |
| **Build Tool** | Apache Maven 3.10+ | Dependency resolution and WAR artifact generation |
| **Testing** | JUnit 5.10, H2 In-Memory Database | Automated unit, concurrency, and integration testing |

---

## 4. Two-Member Team Division

The project is structured into two clearly separated development epics:

| Team Member | Epic Ownership | Core Classes & Artifacts | CRUD Operations Delivered | UI Screens Delivered |
|---|---|---|---|---|
| **Member 1** | **Epic 1: Room Management** | `Room.java` (abstract), `SingleRoom.java`, `DoubleRoom.java`, `SuiteRoom.java`, `RoomDAO.java`, `RoomServlet.java` | 1. Create Room<br>2. Read/Search Rooms<br>3. Update Room<br>4. Delete Room | 1. `room-list.jsp`<br>2. `room-add.jsp`<br>3. `room-edit.jsp` |
| **Member 2** | **Epic 2: Guest & Reservation Management** | `User.java` (abstract), `AdminUser.java`, `RegularGuest.java`, `PremiumGuest.java`, `Reservation.java`, `UserDAO.java`, `ReservationDAO.java`, `UserServlet.java`, `ReservationServlet.java` | 1. Register Guest<br>2. Read/Search Guests & Bookings<br>3. Update Profile & Cancel Booking<br>4. Delete User & Delete Reservation | 1. `register.jsp`<br>2. `login.jsp`<br>3. `profile.jsp`<br>4. `user-list.jsp`<br>5. `reservation-form.jsp`<br>6. `reservation-list.jsp` |

Both members collaboratively developed the database schema (`sql/schema.sql`), security utilities (`PasswordUtil`, `SessionUtil`), and shared documentation.

---

## 5. Demonstration Credentials

All sample accounts are pre-seeded in `sql/schema.sql`:

| Role | Name | Email | Password | Privileges |
|---|---|---|---|---|
| **Administrator** | Kamal Perera | `admin@stayease.com` | `Password@123` | Full admin privileges (Manage rooms, view/delete all bookings, view/delete guests) |
| **Regular Guest** | Nimal Silva | `nimal@gmail.com` | `Password@123` | Standard room booking, 0% discount, view/cancel own bookings |
| **Premium VIP** | Sunil Fernando | `sunil@gmail.com` | `Password@123` | Standard booking + **10% automatic VIP discount** on all room types |

---

## 6. Quick Start & Local Setup

### Step 1: Clone Repository
```bash
git clone <repository-url>
cd "Hotel_managemnt System"
```

### Step 2: Set Up Database (MySQL 8)
1. Ensure MySQL 8 service is running.
2. Execute the database initialization script:
```bash
mysql -u root -p < sql/schema.sql
```
3. Configure database credentials in `src/main/resources/db.properties`:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/stayease_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password
```

### Step 3: Run Automated Tests
```bash
mvn clean test
```
All 24 automated unit, integration, pricing, and concurrency tests will execute and verify the application logic.

### Step 4: Build WAR Package
```bash
mvn package
```
Generates the deployable production WAR file at `target/stayease.war`.

### Step 5: Deploy to Tomcat 10.1
1. Copy `target/stayease.war` into Tomcat's `webapps/` folder.
2. Start Tomcat (`bin/startup.sh` or `bin/startup.bat`).
3. Open your browser and navigate to:  
   **`http://localhost:8080/stayease/`**

---

## 7. Project Documentation Index

Detailed academic documentation is provided in the `docs/` directory:

1. [Architecture & Design Guide](docs/architecture.md) — MVC layered architecture and security design.
2. [Local Setup & Deployment Manual](docs/setup-guide.md) — Comprehensive environment configuration for macOS, Windows, and Linux.
3. [UML Class Diagram](docs/class-diagram.puml) — Complete PlantUML class diagram illustrating OOP inheritance trees and associations.
4. [Requirements Traceability Matrix](docs/requirements-traceability.md) — Mapping every user story to DAO methods, servlets, and UI screens.
5. [Testing & Verification Report](docs/testing.md) — Detailed test cases and automated test results (TC-R-01 to TC-B-08).
6. [Viva Presentation Guide](docs/viva-guide.md) — Step-by-step viva defense walkthrough for both students.
7. [Sprint 0 Progress](docs/sprint-0.md) | [Sprint 1 Progress](docs/sprint-1.md) | [Sprint 2 Progress](docs/sprint-2.md) | [Sprint 3 Progress](docs/sprint-3.md) — Agile sprint artifacts.
