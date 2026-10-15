# Sprint 3: Hardening, Automated Testing & Final Integration

> **Course:** SE1020 – Object Oriented Programming  
> **Period:** 16 Nov 2026 – 29 Nov 2026  
> **Scrum Master:** Member 2  
> **Team:** Member 1 & Member 2  

---

## 1. Sprint Goal
Execute comprehensive automated testing across all user stories and test cases (TC-R-01 through TC-B-08), eliminate edge cases and race conditions, verify cross-platform WAR packaging, and produce all required documentation and viva demonstration materials.

---

## 2. Sprint Backlog & Task Breakdown

| Task ID | Task Description | Owner | Status | Artifact Produced |
|---|---|---|---|---|
| **S3-01** | Build automated JUnit 5 test suite for Epic 1 Room Management | Member 1 | Completed | `RoomTest.java` (8 tests) |
| **S3-02** | Build automated JUnit 5 test suite for Epic 2 User Management | Member 2 | Completed | `UserTest.java` (8 tests) |
| **S3-03** | Build automated JUnit 5 test suite for Epic 2 Reservation & Concurrency | Member 2 | Completed | `ReservationTest.java` (8 tests) |
| **S3-04** | Implement multithreaded concurrent booking stress test (`TC-B-03`) | Both | Completed | `ReservationTest.testMultithreadedConcurrentRaceCondition` |
| **S3-05** | Verify packaging of production WAR file for Tomcat 10.1 deployment | Both | Completed | `target/stayease.war` |
| **S3-06** | Author Requirements Traceability Matrix and Test Report | Member 1 | Completed | `requirements-traceability.md`, `testing.md` |
| **S3-07** | Author System Architecture, Setup Guide, and Viva Defense Guide | Member 2 | Completed | `architecture.md`, `setup-guide.md`, `viva-guide.md` |

---

## 3. Sprint Review & Achievements
* **100% Automated Test Pass Rate:**
  All 24 automated tests executed successfully with 0 failures and 0 errors:
  - `RoomTest`: 8 passed
  - `UserTest`: 8 passed
  - `ReservationTest`: 8 passed
* **Verified Concurrency Protection:** Multithreaded race condition test confirmed that concurrent requests for the same room result in exactly one confirmation while the second is safely rejected.
* **Complete Artifact Deliverables:** Produced production WAR, clean Git directory, complete UML diagrams, and viva defense guide.

---

## 4. Retrospective & Viva Readiness
* **Final Reflection:** The collaborative pair-programming workflow and strict separation of epics enabled both members to contribute equally, master their respective modules, and exceed all assignment requirements.
* **Viva Status:** Both students are fully equipped to demonstrate their code, explain OOP concepts, show live test executions, and answer viva defense questions.
