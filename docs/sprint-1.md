# Sprint 1: Core CRUD & Authentication

> **Course:** SE1020 – Object Oriented Programming  
> **Period:** 19 Oct 2026 – 01 Nov 2026  
> **Scrum Master:** Member 1  
> **Team:** Member 1 & Member 2  

---

## 1. Sprint Goal
Implement the initial CRUD operations and UI screens for both epics: room creation and search for Member 1; guest registration, secure authentication, and session management for Member 2; supported by the shared Bootstrap design system.

---

## 2. Sprint Backlog & Task Breakdown

| Task ID | User Story | Task Description | Owner | Status | Artifact Produced |
|---|---|---|---|---|---|
| **S1-01** | UI Foundation | Build responsive header, footer, alerts, and luxury hospitality CSS | Both | Completed | `style.css`, `header.jsp`, `footer.jsp`, `alerts.jsp` |
| **S1-02** | **R-01** | Implement Room domain models with polymorphic pricing formulas | Member 1 | Completed | `Room.java`, `SingleRoom.java`, `DoubleRoom.java`, `SuiteRoom.java` |
| **S1-03** | **R-01** | Implement `RoomDAO.insert()` with duplicate number detection | Member 1 | Completed | `RoomDAO.java` |
| **S1-04** | **R-01** | Build Add Room servlet action and admin UI screen | Member 1 | Completed | `RoomServlet.java`, `room-add.jsp` |
| **S1-05** | **R-02** | Implement room search & browsing with empty state handling | Member 1 | Completed | `room-list.jsp`, `RoomDAO.search()` |
| **S1-06** | **U-01** | Implement User domain models with polymorphic discount rates | Member 2 | Completed | `User.java`, `AdminUser.java`, `RegularGuest.java`, `PremiumGuest.java` |
| **S1-07** | **U-01** | Implement guest registration with BCrypt hashing and unique email check | Member 2 | Completed | `UserDAO.insert()`, `register.jsp` |
| **S1-08** | **U-02** | Implement session-based login, role redirection, and logout | Member 2 | Completed | `UserServlet.java`, `SessionUtil.java`, `login.jsp` |

---

## 3. Sprint Review & Achievements
* **Member 1 Achievements:** Completed Room models, `RoomDAO` insert and search methods, `RoomServlet`, `room-add.jsp`, and `room-list.jsp`. Verified room duplicate check (TC-R-02) and pricing formulas (TC-R-09).
* **Member 2 Achievements:** Completed User models, `UserDAO` insert and find methods, `UserServlet` registration and login actions, `register.jsp`, and `login.jsp`. Verified duplicate email rejection (TC-U-02) and BCrypt validation (TC-U-04).
* **UI Integration:** Reusable navigation header dynamically detects user sessions and displays appropriate navigation links and role badges.

---

## 4. Retrospective & Action Items
* **What went well:** The separation into `WEB-INF/views/room` and `WEB-INF/views/user` kept Git workspaces completely clean.
* **Challenges faced:** Ensuring that visitors can view rooms without logging in while restricting the Add Room screen strictly to administrators. Solved using `SessionUtil.isAdmin(request)` server-side validation.
* **Action item for Sprint 2:** Complete remaining CRUD operations (Room editing and deletion for Member 1; Profile management, guest search, availability search, booking, and cancellation for Member 2).
