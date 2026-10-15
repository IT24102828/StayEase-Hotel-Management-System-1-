# StayEase Test Suite & Verification Report

This document details the test strategy, automated test scenarios, test cases, and execution results for the StayEase Hotel Room Reservation System.

---

## 1. Test Strategy & Automated Environment

* **Framework:** JUnit 5.10.2 (`org.junit.jupiter`)
* **Test Database:** In-memory H2 database running in `MODE=MySQL` with complete transaction, table lock, and foreign key support.
* **Test Isolation:** Before each test method (`@BeforeEach`), `TestDatabaseHelper.initializeTestDatabase()` re-creates clean tables and populates baseline sample data.
* **Execution Command:**
  ```bash
  mvn clean test
  ```

---

## 2. Test Execution Summary

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.stayease.dao.ReservationTest
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Running com.stayease.model.RoomTest
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Running com.stayease.model.UserTest
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 3. Detailed Test Case Specification & Results

### Epic 1: Room Management (Member 1)

| Test ID | User Story | Test Scenario & Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-R-01** | R-01 | Admin adds room 401, Double, $95.00 base, Available | Room inserted, generated ID returned, nightly rate = $104.50 | Room created with rate $104.50 | **PASS** |
| **TC-R-02** | R-01 | Attempt to insert duplicate room number `101` | Unique constraint triggers `SQLException`, rejected | Duplicate room number throws `SQLException` | **PASS** |
| **TC-R-03** | R-01 | Set base price to $0.00 or -$10.00 | Throws `IllegalArgumentException`, nothing saved | `IllegalArgumentException` thrown | **PASS** |
| **TC-R-04** | R-01 | Non-admin/guest accesses `/rooms?action=new` | Server refuses with HTTP 403 Forbidden | Blocked at server level | **PASS** |
| **TC-R-05** | R-02 | Search with keyword `"Suite"` | Only rooms with type `Suite` returned | All returned rooms have type Suite | **PASS** |
| **TC-R-06** | R-02 | Search with non-existent keyword `"zzz"` | Empty list returned, empty state displayed | Returned list is empty (`size = 0`) | **PASS** |
| **TC-R-07** | R-03 | Update room 102 base price to $70.00 | Room base price and nightly rate updated to $70.00 | Updated successfully in database | **PASS** |
| **TC-R-08** | R-04 | Admin deletes room 302 | Room record removed from database | Query by ID returns `null` | **PASS** |
| **TC-R-09** | R-05 | Polymorphic rates: Double (base 90) and Suite (base 180) | Double = $99.00; Suite = $245.00 ($180 &times; 1.25 + $20) | Exact rates returned: $99.00 and $245.00 | **PASS** |

---

### Epic 2: User Management (Member 2)

| Test ID | User Story | Test Scenario & Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-U-01** | U-01 | Register with valid new email (`kasun@example.com`) | Account created, password hashed with BCrypt | User saved, BCrypt hash verified | **PASS** |
| **TC-U-02** | U-01 | Register with already registered email (`nimal@gmail.com`) | Unique constraint triggers `SQLException` | Duplicate email rejected | **PASS** |
| **TC-U-03** | U-01 | Submit empty or short password | Rejected with `IllegalArgumentException` | Rejected by validation | **PASS** |
| **TC-U-04** | U-02 | Login with wrong password | `PasswordUtil.checkPassword()` returns false | Login refused, no session established | **PASS** |
| **TC-U-05** | U-03 | Update phone while leaving password blank | Phone updated, existing password hash preserved | Phone updated, password still authenticates | **PASS** |
| **TC-U-06** | U-04 | Admin searches guest directory with `"nimal"` | Returns matching user record for Nimal Silva | Record for `nimal@gmail.com` found | **PASS** |
| **TC-U-07** | U-05 | Guest deletes own account | Account record removed from database | User query by ID returns `null` | **PASS** |

---

### Epic 2: Reservation Management (Member 2)

| Test ID | User Story | Test Scenario & Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-B-01** | B-01 | Check-out date before check-in date | Throws `IllegalArgumentException` | Date validation error raised | **PASS** |
| **TC-B-02** | B-01 | Check-in date set to yesterday | Rejected ("Check-in can't be in the past") | Past dates rejected | **PASS** |
| **TC-B-03** | B-02 | Concurrent booking: Two guests book Room 201 for overlapping dates | First booking succeeds; second booking throws `DoubleBookingException` | Overlapping booking refused | **PASS** |
| **TC-B-04** | B-02 | Group of 3 guests attempts to book Double room | Refused because room capacity is 2 | Exceeds capacity check triggers error | **PASS** |
| **TC-B-05** | B-06 | Premium VIP guest books Suite 301 for 3 nights | Total is strictly **$661.50** ($245 &times; 3 = $735 &minus; 10%) | Exact calculated total is $661.50 | **PASS** |
| **TC-B-06** | B-04 | Guest cancels reservation, then searches same dates | Cancelled status set; room becomes available again | Room appears in availability search | **PASS** |
| **TC-B-07** | B-03 | Guest A queries reservations list | Guest B's reservations are strictly excluded | Guest isolation verified | **PASS** |
| **TC-B-08** | B-05 | Admin deletes reservation record | Reservation permanently removed from database | Query by ID returns `null` | **PASS** |

---

## 4. Key Verification Highlights

### Concurrency Stress Test (TC-B-03)
A multithreaded stress test using `ExecutorService` and `CountDownLatch` dispatched two simultaneous booking threads attempting to reserve Room 102 for the identical date range:
* **Result:** Thread A successfully committed its reservation while Thread B was safely rejected with `DoubleBookingException`.
* **Zero double bookings were confirmed.**

### Exact Mathematical Pricing Verification (TC-B-05)
* Room: Suite 301 (Base Price = $180.00)
* Nightly Rate: `180.00 * 1.25 + 20.00 = $245.00`
* Stay: 3 nights &rarr; Gross Total: `$245.00 * 3 = $735.00`
* User: Premium VIP &rarr; Discount Rate: `10% ($73.50)`
* Net Total: `$735.00 - $73.50 = $661.50`
* **Test Result:** Asserted `$661.50` &mdash; matched to the exact cent.
