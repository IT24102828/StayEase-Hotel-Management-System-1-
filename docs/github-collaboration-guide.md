# GitHub Collaboration & Branching Guide for Two Students

This guide explains how both team members set up Git, create feature branches, commit their assigned modules, open pull requests, and maintain a clean commit history for academic submission and viva verification.

---

## 1. Branch Strategy

The repository follows standard Git Feature-Branch workflow:

* **`main`**: Protected production branch containing stable, reviewed, and tested code.
* **`feature/epic1-rooms`**: Development branch for **Member 1** (Room Management epic).
* **`feature/epic2-guests-reservations`**: Development branch for **Member 2** (Guest and Reservation Management epic).

```
   (main) -----------------●--------------------●-------------------● (Final Release)
                            \                  / \                 /
  (feature/epic1-rooms)      ●------●---------●   \               /  (Member 1 PR)
                              \                    \             /
  (feature/epic2-guests)       ●--------------------●-----------●   (Member 2 PR)
```

---

## 2. Step-by-Step Git Workflow for Both Students

### Step A: Repository Setup on GitHub (Student 1)
1. Create a new repository on GitHub named: `hotel-room-reservation-system` (Private or Public as required by lecturer).
2. Add Student 2 as a **Collaborator** under **Settings &rarr; Collaborators**.
3. Initialize the repository locally and push the project skeleton:
   ```bash
   git init
   git add .
   git commit -m "chore: initial project skeleton with maven, schema, and documentation"
   git branch -M main
   git remote add origin https://github.com/<username>/hotel-room-reservation-system.git
   git push -u origin main
   ```

### Step B: Student 2 Clones the Repository
Student 2 clones the repository on their own computer:
```bash
git clone https://github.com/<username>/hotel-room-reservation-system.git
cd hotel-room-reservation-system
```

---

## 3. Member 1 Workflow (Epic 1: Room Management)

### 1. Create and Switch to Feature Branch
```bash
git checkout -b feature/epic1-rooms
```

### 2. Verify Your Git Author Identity
Ensure your commits reflect your own university student identity:
```bash
git config user.name "Your Name (Member 1)"
git config user.email "your.student.email@sliit.lk"
```

### 3. Stage and Commit Your Epic Files
Commit in meaningful logical increments:

**Commit 1: Models & OOP Pricing**
```bash
git add src/main/java/com/stayease/model/Room.java \
        src/main/java/com/stayease/model/SingleRoom.java \
        src/main/java/com/stayease/model/DoubleRoom.java \
        src/main/java/com/stayease/model/SuiteRoom.java
git commit -m "feat(room): implement Room domain model hierarchy with polymorphic pricing formulas"
```

**Commit 2: Data Access & Controller**
```bash
git add src/main/java/com/stayease/dao/RoomDAO.java \
        src/main/java/com/stayease/servlet/RoomServlet.java
git commit -m "feat(room): implement RoomDAO JDBC operations and RoomServlet controller"
```

**Commit 3: JSP Views**
```bash
git add src/main/webapp/WEB-INF/views/room/
git commit -m "feat(room): create room-list, room-add, and room-edit JSP views"
```

**Commit 4: Automated Tests**
```bash
git add src/test/java/com/stayease/model/RoomTest.java
git commit -m "test(room): add JUnit 5 test cases TC-R-01 through TC-R-09"
```

### 4. Push Branch to GitHub
```bash
git push -u origin feature/epic1-rooms
```

---

## 4. Member 2 Workflow (Epic 2: Guest & Reservation Management)

### 1. Create and Switch to Feature Branch
```bash
git checkout -b feature/epic2-guests-reservations
```

### 2. Verify Your Git Author Identity
```bash
git config user.name "Your Name (Member 2)"
git config user.email "your.student.email@sliit.lk"
```

### 3. Stage and Commit Your Epic Files

**Commit 1: User Models & Security**
```bash
git add src/main/java/com/stayease/model/User.java \
        src/main/java/com/stayease/model/AdminUser.java \
        src/main/java/com/stayease/model/RegularGuest.java \
        src/main/java/com/stayease/model/PremiumGuest.java \
        src/main/java/com/stayease/util/PasswordUtil.java
git commit -m "feat(user): implement User model hierarchy and BCrypt password hashing"
```

**Commit 2: Reservation Model & Transaction Locking**
```bash
git add src/main/java/com/stayease/model/Reservation.java \
        src/main/java/com/stayease/dao/ReservationDAO.java \
        src/main/java/com/stayease/util/DoubleBookingException.java
git commit -m "feat(reservation): implement ReservationDAO with FOR UPDATE row-locking transaction"
```

**Commit 3: Controllers and Views**
```bash
git add src/main/java/com/stayease/servlet/UserServlet.java \
        src/main/java/com/stayease/servlet/ReservationServlet.java \
        src/main/webapp/WEB-INF/views/user/ \
        src/main/webapp/WEB-INF/views/reservation/
git commit -m "feat(views): create guest management, availability search, and booking JSPs"
```

**Commit 4: Automated Tests & Concurrency Verification**
```bash
git add src/test/java/com/stayease/model/UserTest.java \
        src/test/java/com/stayease/dao/ReservationTest.java
git commit -m "test(reservation): add test cases TC-U-01 to TC-U-07 and TC-B-01 to TC-B-08 with concurrency test"
```

### 4. Push Branch to GitHub
```bash
git push -u origin feature/epic2-guests-reservations
```

---

## 5. Pull Request & Review Process

1. On GitHub, navigate to **Pull requests &rarr; New pull request**.
2. Select base: `main` &larr; compare: `feature/epic1-rooms`.
3. Member 2 reviews Member 1's code changes, comments, and approves the Pull Request.
4. Merge using **Create a merge commit** (preserving distinct branch history).
5. Open second Pull Request from `feature/epic2-guests-reservations` into `main`.
6. Member 1 reviews and approves.
7. Merge into `main`.

---

## 6. How to Avoid and Resolve Merge Conflicts

* **Update Local Main Regularly:**
  Before starting new work, pull the latest changes:
  ```bash
  git checkout main
  git pull origin main
  git checkout feature/your-branch
  git merge main
  ```
* **Separation of Files:**
  Notice that Member 1 owns `com/stayease/model/Room*` and `views/room/`, while Member 2 owns `com/stayease/model/User*`, `Reservation*`, and `views/user/` / `views/reservation/`. This strict architectural boundary prevents merge conflicts in business logic.
* **Shared Files:**
  Shared files like `header.jsp` or `DBConnection.java` should only be changed after communicating with your partner during sprint stand-ups.
