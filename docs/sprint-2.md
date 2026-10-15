# Sprint 2: Remaining CRUD, Availability & Concurrency Booking

> **Course:** SE1020 – Object Oriented Programming  
> **Period:** 02 Nov 2026 – 15 Nov 2026  
> **Scrum Master:** Member 2  
> **Team:** Member 1 & Member 2  

---

## 1. Sprint Goal
Complete all remaining CRUD operations for both team members, including room editing and deletion for Member 1, and guest profile management, guest search, room availability searching, transactional booking with row-locking, and reservation cancellation for Member 2.

---

## 2. Sprint Backlog & Task Breakdown

| Task ID | User Story | Task Description | Owner | Status | Artifact Produced |
|---|---|---|---|---|---|
| **S2-01** | **R-03** | Implement Room update logic with pre-filled form and validation | Member 1 | Completed | `RoomDAO.update()`, `room-edit.jsp` |
| **S2-02** | **R-04** | Implement Room deletion with modal confirmation and cascade handling | Member 1 | Completed | `RoomDAO.delete()`, `room-list.jsp` |
| **S2-03** | **R-06** | Integrate Maintenance room filtering to exclude from booking queries | Member 1 | Completed | `RoomDAO.findAvailable()` |
| **S2-04** | **U-03** | Implement profile management (update name, phone, password, membership) | Member 2 | Completed | `profile.jsp`, `UserDAO.update()` |
| **S2-05** | **U-04** | Implement admin guest search by name, email, or user ID | Member 2 | Completed | `user-list.jsp`, `UserDAO.search()` |
| **S2-06** | **U-05** | Implement account deletion (guest self-delete vs. admin deleting others) | Member 2 | Completed | `UserServlet.java`, `UserDAO.delete()` |
| **S2-07** | **B-01** | Implement date availability search (1–30 nights, 1–10 guests, non-past check-in) | Member 2 | Completed | `reservation-form.jsp` |
| **S2-08** | **B-02** | Implement atomic reservation insertion with `FOR UPDATE` row-locking | Member 2 | Completed | `ReservationDAO.insert()`, `DoubleBookingException` |
| **S2-09** | **B-03** | Implement reservation list with guest privacy isolation | Member 2 | Completed | `reservation-list.jsp`, `ReservationDAO.findByUser()` |
| **S2-10** | **B-04** | Implement reservation cancellation (restores room availability) | Member 2 | Completed | `ReservationDAO.cancel()` |
| **S2-11** | **B-05** | Implement permanent reservation record deletion for administrators | Member 2 | Completed | `ReservationDAO.delete()` |
| **S2-12** | **B-06** | Implement polymorphic 10% VIP discount on total booking calculation | Member 2 | Completed | `Reservation.calculateTotalPrice()` |

---

## 3. Sprint Review & Achievements
* **Full CRUD Achieved for Both Members:**
  - **Member 1 (Epic 1):** Room Create, Read/Search, Update, Delete. Exceeded minimum 3 CRUD operations (delivered 4) and 3 screens (`room-list.jsp`, `room-add.jsp`, `room-edit.jsp`).
  - **Member 2 (Epic 2):** User Create (register), Read (search), Update (profile), Delete (account); Reservation Create (book), Read (list), Update (cancel), Delete (admin delete). Exceeded minimum 3 CRUD operations (delivered 8) and 3 screens (delivered 6).
* **Guaranteed Concurrency Safety:** Implemented `SELECT ... FOR UPDATE` inside `ReservationDAO.insert()` to prevent double bookings during simultaneous submissions.
* **Polymorphic Calculations Verified:** Verified that booking Suite 301 for 3 nights by a Premium VIP guest produces exactly $661.50 (TC-B-05).

---

## 4. Retrospective & Action Items
* **What went well:** Database transaction rollbacks cleanly handle race conditions and restore connection auto-commit mode in `finally` blocks.
* **Challenges faced:** When booking dates overlap with checkout days, back-to-back bookings must be allowed. Solved using exclusive check-out boundary comparison (`check_in < requested.check_out AND check_out > requested.check_in`).
* **Action item for Sprint 3:** Hardening, automated JUnit testing, documentation finalization, and viva defense preparation.
