# Sprint 0: Project Setup, Architecture & Foundation

> **Course:** SE1020 – Object Oriented Programming  
> **Period:** 12 Oct 2026 – 18 Oct 2026  
> **Scrum Master:** Member 1  
> **Team:** Member 1 & Member 2  

---

## 1. Sprint Goal
Establish the foundational infrastructure, Maven project structure, Jakarta EE web configurations, MySQL 8 schema with initial seed data, and detailed Object-Oriented design artifacts for the StayEase Hotel Room Reservation System.

---

## 2. Sprint Backlog & Task Breakdown

| Task ID | Task Description | Owner | Status | Artifact Produced |
|---|---|---|---|---|
| **S0-01** | Analyze assignment guidelines, charter, and requirements specification | Both | Completed | Traceability checklist, Sprint plan |
| **S0-02** | Configure Maven `pom.xml` with Java 17, Jakarta Servlet 6.0, JSTL 3.0, MySQL Connector, BCrypt, and JUnit 5 | Member 1 | Completed | `pom.xml`, `.gitignore` |
| **S0-03** | Design relational database schema and seed data | Member 2 | Completed | `sql/schema.sql` |
| **S0-04** | Design OOP domain model hierarchy (Room & User inheritance) | Member 1 | Completed | `docs/class-diagram.puml` |
| **S0-05** | Implement JDBC connection manager and configuration loader | Both | Completed | `DBConnection.java`, `db.properties` |
| **S0-06** | Implement secure password hashing utility with BCrypt | Member 2 | Completed | `PasswordUtil.java` |
| **S0-07** | Establish Git branching strategy and repository conventions | Both | Completed | Git collaboration workflow documentation |

---

## 3. Sprint Review & Achievements
* **Verified Maven Build:** Successfully resolved dependencies and compiled foundational classes on OpenJDK 21/Java 17.
* **Database Schema Finalized:** Created tables for `users`, `rooms`, and `reservations` with foreign key cascades, date integrity checks, and composite indexes.
* **Seed Data:** Populated demonstration data featuring 3 user roles (Admin, Regular, Premium), 7 rooms across all room types, and 2 active bookings.
* **OOP Architecture Documented:** UML class diagram visualizes encapsulation, inheritance trees, polymorphic rates, and factory patterns.

---

## 4. Retrospective & Action Items
* **What went well:** Clear task division between Member 1 (Room epic infrastructure) and Member 2 (User/Reservation infrastructure) avoided code conflicts.
* **Challenges faced:** Ensuring Jakarta EE 10 compatibility (`jakarta.servlet.*`) rather than legacy `javax.servlet.*` required careful dependency coordinates.
* **Action item for Sprint 1:** Proceed with Core CRUD implementation (Room creation and listing for Member 1; Guest registration and login for Member 2).
